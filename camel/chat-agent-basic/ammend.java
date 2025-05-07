//DEPS dev.langchain4j:langchain4j-open-ai:0.33.0

//DEPS com.itextpdf:html2pdf:6.1.0
//DEPS com.itextpdf:itext7-core:9.1.0@pom

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.RegexBasedLocationExtractionStrategy;


import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
// import org.w3c.dom.Node;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import static java.time.Duration.ofSeconds;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ammend extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Routes are loaded from YAML files
    }

    private static String LLM_URL;

    @PropertyInject("llm.url")
    public void setLlmUrl(String url) {
        LLM_URL = url;
    }

    public static String getLlmUrl() {
        return LLM_URL;
    }

    @BindToRegistry(lazy=true)
    public static ChatLanguageModel chatModelAmmend(){

        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey("EMPTY")
            // .modelName("qwen2.5:3b-instruct")
            // .modelName("qwen2.5:7b-instruct")
            .modelName("qwen2.5:14b-instruct")
            .baseUrl("http://"+getLlmUrl()+"/v1/")
            .temperature(0.0)
            .timeout(ofSeconds(180))
            .logRequests(true)
            .logResponses(true)
            .build();

        return model;
    }


    @BindToRegistry(lazy=true)
    public static Processor ammendInvoiceMetadata(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();


                String systemMessage = """
                        You are an assistant to help fixing invoices.
    
                        The invoice will come in JSON format.

                        Seller and Client contain information about the two parts involved in the purchase.
                        Items contains a list of products purchased. Each Item instance represents a row in the table.
                        Summary contains the total cost calculation of all the products purchased.

                        Modify the JSON payload as needed as per the user request.

                        Provide the output as JSON.

                        Do not insert comments.
     
                        Do not use ``` (backticks), just return the raw JSON value.
                        """;

                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(payload));

                exchange.getIn().setBody(messages);
            }
        };
    }


    @BindToRegistry(lazy=true)
    public static Processor InvoiceToPDF(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String markdown = exchange.getMessage().getBody(String.class);
        
                // Add basic CSS for table styling
                String css = """
                        <style>
                            @import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@300..800&display=swap');

                            table {
                              width: 100%;
                              border: 2px solid #cccccc; /* Gray outer border */
                              border-spacing: 0; /* Remove gaps between cells */
                              font-size: 12px; /* Reduce overall font size */
                            }
                            th, td {
                              border: none; /* No vertical borders */
                              border-bottom: 2px solid #cccccc; /* Gray horizontal row separator */
                              padding: 10px; /* Padding for readability */
                            }
                            /* Ensure header has bottom border */
                            tr:first-child th {
                              border-bottom: 2px solid #cccccc; /* Explicitly set header bottom border */
                            }
                            tr:first-child td {
                              border-top: 2px solid #cccccc; /* Explicitly set header bottom border */
                            }

                            table tr th:nth-child(n+3), table tr td:nth-child(n+3) {
                              text-align: right;
                            }

                            /* Remove border-bottom from the last row to avoid extra line */
                            tr:last-child th, tr:last-child td {
                              border-bottom: none;
                            }
                            th {
                              background-color: #ffffff; /* White header */
                              text-align: left;
                            }
                            tr:nth-child(even) {
                              background-color: #ffffff; /* White for even rows */
                            }
                            tr:nth-child(odd) {
                              background-color: #e9e9e9; /* Darker gray for odd rows */
                            }
                            tr:first-child {
                              background-color: #e9e9e9; /* Header stays white */

                            }

                            body {
                            //   padding-top: 10px;
                              margin: 20px; /* Reserve space for the right margin */

                              font-family: "Open Sans", sans-serif;
                              font-size: 14px;
                              line-height: 110%;

                              
                            //   transform: scale(1, .8);
                            //   font-weight: 1.5;

                            height: 100vh;
                            position: relative;

                            }
                            .right-stripe {
                                // position: fixed;
                                // top: 0;
                                // right: 20px;
                                // width: 10px;
                                // height: 100%;
                                // background-color: rgba(128, 128, 128, 0.5);
                                // z-index: 1000;

                                position: absolute;
                                top: 0;
                                right: 20px;
                                width: 10px;
                                height: 100%;
                                background-color: rgba(128, 128, 128, 0.5);
                                z-index: 1000;

                            }

                        </style>
                        """;

                String html = css + markdown;

                System.out.println("HTML:\n"+html);

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                HtmlConverter.convertToPdf(html, os);

                
                exchange.getIn().setBody(addRectangleToPdf(os.toByteArray()));
                // exchange.getIn().setBody(os);
            }
        };
    }

    public static ByteArrayOutputStream addRectangleToPdf(byte[] inputPdf) throws IOException {
        // Create a ByteArrayInputStream from the input byte array
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputPdf);
        
        // Create a ByteArrayOutputStream for the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Initialize PdfReader and PdfWriter
        PdfReader reader = new PdfReader(inputStream);
        PdfWriter writer = new PdfWriter(outputStream);
        
        // Create PdfDocument for reading and writing
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        
        // Get the first page
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        
        // Get page size to dynamically adjust the stripe
        Rectangle pageSize = pdfDoc.getFirstPage().getPageSize();
        float pageWidth = pageSize.getWidth();
        float pageHeight = pageSize.getHeight();
        
        drawSectionMarker("Seller", pdfDoc, canvas);
        drawSectionMarker("ITEMS", pdfDoc, canvas);
        drawSectionMarker("SUMMARY", pdfDoc, canvas);

        // Define the stripe: 10 points wide, in the right margin (36 points from right edge)
        float stripeWidth = 10;
        float margin = 30;
        float x = pageWidth - margin - stripeWidth; // Right margin
        float yBottom = 0;//margin; // 36 points from bottom
        float yTop = pageHeight;// - margin; // 36 points from top
        
        // Define the rectangle for the stripe
        Rectangle stripe = new Rectangle(x, yBottom, stripeWidth, yTop - yBottom);
        
        // Set fill color to gray (0.5f = medium gray)
        canvas.setFillColor(new DeviceGray(0.8f));

        // Set line width for the border
        canvas.setLineWidth(2);
            
        // Draw the stripe (border only)
        canvas.rectangle(stripe.getX(), stripe.getY(), stripe.getWidth(), stripe.getHeight());
        canvas.fill();
        
        // Close the document
        pdfDoc.close();
        
        // Close streams
        reader.close();
        
        return outputStream;
    }

    public static void drawSectionMarker(String regex, PdfDocument pdfDoc, PdfCanvas canvas){

        // Extract text locations from the first page
        RegexBasedLocationExtractionStrategy strategy = new RegexBasedLocationExtractionStrategy(regex); // Match any text
        PdfCanvasProcessor processor = new PdfCanvasProcessor(strategy);
        processor.processPageContent(pdfDoc.getFirstPage());
        Collection<IPdfTextLocation> textLocationsCollection = strategy.getResultantLocations();
        List<IPdfTextLocation> textLocations = new ArrayList<>(textLocationsCollection);
        
        // Default stripe parameters if no text is found
        float stripeWidth = 45;
        float margin = 0;
        float x = margin; // Left margin (x=36)
        float yBottom = margin; // Fallback: 36 points from bottom
        float height = 8; // Fallback height if no text is found
        
        // If text is found, use the first text chunk's bounding box
        if (!textLocations.isEmpty()) {
            IPdfTextLocation firstText = textLocations.get(0); // Use first text chunk
            Rectangle textRect = firstText.getRectangle();
            yBottom = textRect.getBottom() + 4; // Align stripe with text's bottom
            // height = textRect.getHeight() - 5; // Use text's height
        }
        
        // Define the stripe: 10 points wide, in the left margin, height of text
        Rectangle stripe = new Rectangle(x, yBottom, stripeWidth, height);
        
        // Set fill color to gray (0.5f = medium gray)
        canvas.setFillColor(new DeviceGray(0.8f));
        
        // Draw the stripe (filled with gray)
        canvas.rectangle(stripe.getX(), stripe.getY(), stripe.getWidth(), stripe.getHeight());
        canvas.fill();
    }
}
