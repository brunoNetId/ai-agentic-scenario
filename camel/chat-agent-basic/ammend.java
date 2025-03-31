//DEPS dev.langchain4j:langchain4j-open-ai:0.33.0

//DEPS com.vladsch.flexmark:flexmark-all:0.64.8
//DEPS com.itextpdf:html2pdf:4.0.5

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.itextpdf.html2pdf.HtmlConverter;

// import java.io.ByteArrayOutputStream;
// import java.io.OutputStream;
// import java.util.Collections;

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
import java.util.ArrayList;
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
    public static Processor ammendInvoice(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();



                // You are an assistant to help fixing invoices.
    
                // There is a mistake in the invoice. There is an item missing, a bottle of wine Rioja 2020.
                // Please fix the invoice.

                // <div style="position: absolute; top: 20px; right: 20px; background-color: rgba(255, 0, 0, 0.7); color: white; padding: 5px 20px; font-weight: bold; transform: rotate(15deg); font-size: 14px; box-shadow: 0 0 10px rgba(0,0,0,0.3);">Amended</div>

                String systemMessage = """
                        You are an assistant to help fixing invoices.
    
                        The input is Markdown.
                        Provide the output as Markdown.
     
                        Apply the following layout when rendering the information:

                            <div style="position: relative; float: right; margin-top: 20px; margin-right: 20px; background-color: rgba(255, 0, 0, 0.7); color: white; padding: 5px 20px; font-weight: bold; transform: rotate(15deg); font-size: 14px; box-shadow: 0 0 10px rgba(0,0,0,0.3);">Amended</div>

                            ## Invoice No: (number)
                            Date of issue: (today)

                            <br><br><br><br><br>

                            <div style="display: flex; width: 100%;">
                                <div style="vertical-align: top; padding-right: 40px;">
                                    <div style="font-weight: bold; font-size: 1.2em; margin-bottom: 5px;">Seller:</div>
                                    
                                </div>
                                <div style="vertical-align: top; padding-right: 40px;">
                                    <div style="font-weight: bold; font-size: 1.2em; margin-bottom: 5px;">Client:</div>
                                    
                                </div>
                            </div>

                            ### ITEMS

                            ### SUMMARY
                           
                        Do not use ``` (backticks), just return the raw Markdown value.
                        Only return the HTML content, do not include comments.
                        """;

                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(payload));

                exchange.getIn().setBody(messages);
            }
        };
    }


    @BindToRegistry(lazy=true)
    public static Processor MarkdownToPDF(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String markdown = exchange.getMessage().getBody(String.class);
        
                // Set up Flexmark with table extension
                MutableDataSet options = new MutableDataSet();
                options.set(Parser.EXTENSIONS, Collections.singletonList(TablesExtension.create()));
                
                Parser parser = Parser.builder(options).build();
                HtmlRenderer renderer = HtmlRenderer.builder(options).build();
                Node document = parser.parse(markdown);
                
                // Add basic CSS for table styling
                // String css = "<style>" +
                //             "table { border-collapse: collapse; width: 100%; }" +
                //             "th, td { border: 1px solid black; padding: 5px; text-align: left; }" +
                //             "th { background-color: #f2f2f2; }" +
                //             "</style>";

                String css = """
                        <style>
                            table { border-collapse: collapse; width: 100%; }
                            th, td { border: 1px solid black; padding: 5px; text-align: left; }
                            th { background-color: #f2f2f2; }
                        </style>
                        """;
                       
                String html = css + renderer.render(document);

                // System.out.println("HTML:\n"+html);

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                HtmlConverter.convertToPdf(html, os);

                exchange.getIn().setBody(os);
            }
        };
    }


}
