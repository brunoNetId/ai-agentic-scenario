//DEPS com.vladsch.flexmark:flexmark-all:0.64.8
//DEPS com.itextpdf:html2pdf:4.0.5

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.itextpdf.html2pdf.HtmlConverter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collections;


import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

public class markdowntopdf extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Routes are loaded from YAML files
    }


    @BindToRegistry(lazy=true)
    public static Processor MarkdownToPDF(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {


                String markdown = """
                    ## Invoice no: 40378170
    
                    <style>
                    td, th, table {
                    border: none!important;
                    background-color:white;
                    }
                    </style>
    
    
    
                    | Seller | Client |
                    |---------|-----------|
                    | Patel, Thompson and Montgomery| Jackson, Odonnell and Jackson 
                    | 356 Kyle Vista                | 267 John Track Suite 841  
                    | New James, MA 46228           | Jenniferville, PA 98601 
                    | Tax Id: 958-74-3511           | Tax Id: 998-87-7723  
                    | IBAN: GB77WRBQ31965128414006  | 
    
                    <style>
                    td, th, table {
                    border: ;
                    background-color: red;
                    }
                    </style>
    
                    Date of issue:
                    10/15/2012
    
                    ## Seller:
                    Patel, Thompson and Montgomery\n
                    356 Kyle Vista\n
                    New James, MA 46228\n
                    Tax Id: 958-74-3511\n
                    IBAN: GB77WRBQ31965128414006\n
    
                    ## Client:  
                    Jackson, Odonnell and Jackson  
                    267 John Track Suite 841  
                    Jenniferville, PA 98601  
                    Tax Id: 998-87-7723  
    
                    ## ITEMS  
    
                    | No. | Description                                                                 | Qty  | UM   | Net price | Net worth | VAT [%] | Gross worth |
                    |-----|-----------------------------------------------------------------------------|------|------|-----------|-----------|---------|-------------|
                    | 1   | Leed's Wine Companion Bottle Corkscrew Opener Gift Box Set with Foil Cutter | 2,00 | each | 7,50      | 15,00     | 10%     | 16,50       |
    
                    ## SUMMARY  
    
                    | VAT [%] | Net worth | VAT   | Gross worth |
                    |---------|-----------|-------|-------------|
                    | 10%     | 15,00     | 1,50  | 16,50       |
                    | Total   | $15,00    | $1,50 | $16,50      |
                    """;
    

                markdown = """
                        ## Invoice no: 40378170
        
                        | Seller | Client |
                        |---------|-----------|
                        | Patel, Thompson and Montgomery| Jackson, Odonnell and Jackson 
                        | 356 Kyle Vista                | 267 John Track Suite 841  
                        | New James, MA 46228           | Jenniferville, PA 98601 
                        | Tax Id: 958-74-3511           | Tax Id: 998-87-7723  
                        | IBAN: GB77WRBQ31965128414006  | 
                        | Tax Id: 958-74-3511           | Tax Id: 998-87-7723  
                        
                        Date of issue:
                        10/15/2012
        
                        ## Seller:
                        Patel, Thompson and Montgomery\n
                        356 Kyle Vista\n
                        New James, MA 46228\n
                        Tax Id: 958-74-3511\n
                        IBAN: GB77WRBQ31965128414006\n
        
                        ## Client:  
                        Jackson, Odonnell and Jackson  
                        267 John Track Suite 841  
                        Jenniferville, PA 98601  
                        Tax Id: 998-87-7723  
        
                        ## ITEMS  
        
                        | No. | Description                                                                 | Qty  | UM   | Net price | Net worth | VAT [%] | Gross worth |
                        |-----|-----------------------------------------------------------------------------|------|------|-----------|-----------|---------|-------------|
                        | 1   | Leed's Wine Companion Bottle Corkscrew Opener Gift Box Set with Foil Cutter | 2,00 | each | 7,50      | 15,00     | 10%     | 16,50       |
        
                        ## SUMMARY  
        
                        | VAT [%] | Net worth | VAT   | Gross worth |
                        |---------|-----------|-------|-------------|
                        | 10%     | 15,00     | 1,50  | 16,50       |
                        | Total   | $15,00    | $1,50 | $16,50      |
                        """;

                markdown = exchange.getMessage().getBody(String.class);


        
                // Set up Flexmark with table extension
                MutableDataSet options = new MutableDataSet();
                options.set(Parser.EXTENSIONS, Collections.singletonList(TablesExtension.create()));
                
                Parser parser = Parser.builder(options).build();
                HtmlRenderer renderer = HtmlRenderer.builder(options).build();
                Node document = parser.parse(markdown);
                
                // Add basic CSS for table styling
                String css = "<style>" +
                            "table { border-collapse: collapse; width: 100%; }" +
                            "th, td { border: 1px solid black; padding: 5px; text-align: left; }" +
                            "th { background-color: #f2f2f2; }" +
                            "</style>";
                String html = css + renderer.render(document);
        
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                HtmlConverter.convertToPdf(html, os);





                exchange.getIn().setBody(os);
            }
        };
    }


}
