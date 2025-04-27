//DEPS dev.langchain4j:langchain4j-open-ai:0.33.0

import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import static java.time.Duration.ofSeconds;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.ExchangeBuilder;

public class planner extends RouteBuilder {

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
    public static ChatLanguageModel chatModelPlanner(){

        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey("EMPTY")
            // .modelName("qwen2.5-coder:3b")
            // .modelName("qwen2.5:0.5b-instruct")
            // .modelName("qwen2.5:3b-instruct")
            .modelName("qwen2.5:7b-instruct")
            // .modelName("qwen2.5:14b-instruct")
            // .modelName("gemma3:4b")
            .baseUrl("http://"+getLlmUrl()+"/v1/")
            .temperature(0.0)
            .timeout(ofSeconds(180))
            .logRequests(true)
            .logResponses(true)
            // .responseFormat("JSON")
            .build();

        return model;
    }

    private static String activity = "";

    @BindToRegistry(lazy=true)
    public static Processor createChatMessagePlanner(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                //reset activity
                activity = "";

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();

                    // Note the comments in the parameter comments, as per the pattern above.


    // More actions can be nested if the sentences are connected in meaning between each other.
                String systemMessage = """
                    Please provide an execution plan.
                    When the content is simple enough, just define a single step.
                    If the output of a step is needed as input in another step, flag the dependency between the steps.

                    JSON pattern:
                    {
                        "steps": [
                            {
                                "id": numeric id starts at 0,
                                "description": a description (should contain all the key details of the original segment)
                                "dependencies": not mandatory
                                "functions": which functions map to this action
                                "parameters": {   (key parameters relevant to the description)
                                    some parameters here, do not include comments (//), use the field comments instead.
                                    "id": 2,
                                    "comments": "Assuming there is a book-related promotion with ID 2, adjust as necessary"
                                }
                            }
                        ]
                    }

                    Note that JSON does not support comments (double slash), like: // This is a comment.
                    Do not include comments (//) in the JSON output.

                    Do not exclude crucial information needed by the executor, the fields 'description' and 'parameters' should contain all the relevant information to execute the task.

                    Return the RAW JSON data, no code block Markdown envelope.
                    Only provide the plan in JSON format without extra comments.

                    When generating the execution plan, take in account the following tools are available downstream:

                    {
                      "tools": [
                        {
                        "type": "function",
                        "function": {
                            "name": "getHTMLLinkToPDFDocumentForGivenInvoiceIdentifier",
                            "description": "get HTML link to PDF document for given invoice identifier",
                            "parameters": {
                            "type": "object",
                            "properties": {
                                "invoiceid": {
                                "type": "string"
                                }
                            },
                            "required": [
                                "invoiceid"
                            ]
                            }
                        }
                        },
                        {
                        "type": "function",
                        "function": {
                            "name": "amendInvoiceWithUserInstructionsDescribedInParameterMessage",
                            "description": "amend invoice with user instructions described in parameter message",
                            "parameters": {
                            "type": "object",
                            "properties": {
                                "invoiceid": {
                                "type": "string"
                                },
                                "message": {
                                "type": "string"
                                }
                            },
                            "required": [
                                "invoiceid",
                                "message"
                            ]
                            }
                        }
                        },
                        {
                        "type": "function",
                        "function": {
                            "name": "getActivePromotions",
                            "description": "get active promotions",
                            "parameters": {
                            "type": "object",
                            "properties": {},
                            "required": []
                            }
                        }
                        },
                        {
                        "type": "function",
                        "function": {
                            "name": "awardsAPromotionToACustomer",
                            "description": "awards a promotion to a customer",
                            "parameters": {
                            "type": "object",
                            "properties": {
                                "invoiceid": {
                                "type": "number"
                                },
                                "promotionid": {
                                "type": "number"
                                }
                            },
                            "required": [
                                "invoiceid",
                                "promotionid"
                            ]
                            }
                        }
                        },
                        {
                        "type": "function",
                        "function": {
                            "name": "GetListOfInvoicesForPurchasesOfATypeOfProduct",
                            "description": "Get list of invoices for purchases of a type of product",
                            "parameters": {
                            "type": "object",
                            "properties": {
                                "productType": {
                                "type": "String"
                                }
                            },
                            "required": [
                                "productType"
                            ]
                            }
                        }
                        }
                    ]
                    }


                    """;


                    // Do not include field "actions" having an empty array like "actions":[]

                    // Please discard ```json notations.

                    // Unwrap the Markdown code block, the user is only interested in the raw data.

                String user = payload;

                // user = "Search for PDF documents from January 2024 to July. Tell me the temperature in London.";
                // user = "Search for PDF documents from January 2024 to July.";
                // user = "Find all the PDF documents from January 2024 to July. For each one of the documents extract the name and surname metadata, and then create an entry in the database using the metadata obtained.";
                // user = "Find all the PDF documents from January 2024 to July. For each one of the documents extract the name and surname metadata";
                // // user = "Find all the PDF documents from January 2024 to July.";

                messages.add(new SystemMessage(systemMessage));
                // messages.add(new UserMessage(payload));
                messages.add(new UserMessage(user));

                exchange.getIn().setBody(messages);
            }
        };
    }


    @BindToRegistry(lazy=true)
    public static Processor callAction(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String body = exchange.getIn().getBody(String.class);

                String input = exchange.getVariable("input", String.class);

                if (input != null){
                    body += "\nUse the following input: " + input;
                }

                Exchange action = ExchangeBuilder.anExchange(exchange.getContext())
                    .withBody(body)
                    .build();

                action.setVariable("sub-actions", exchange.getVariable("sub-actions"));

                Exchange result = exchange.getContext().createProducerTemplate().send("direct:action", action);
            }
        };
    }

    @BindToRegistry(lazy=true)
    public static Processor createChatMessagePlanSummary(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                //reset activity
                activity = "";

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();


                // Don't use Markdown, use HTML instead. Use bullet points to display the summary. Make sure the bullet point is followed by text, not a new line.


                // String systemMessage = """
                //     You provide a summary of the plan execution.

                //     Based on the inputs, create a summary of the requests processed and their results.
                //     Don't use Markdown, use HTML instead. Use bullet points to display the summary, as follows:

                //         <ul>
                //             <li>bullet point</li>
                //             <li>others</li>
                //         </ul>

                //     When a result contains an iframe with an invoice URL, please format into a foldable HTML entity, as follows:

                //         <details>
                //             <summary style="color:blue">Click to show PDF</summary>
                //             <iframe>url</iframe>
                //         </details>

                //     The intention is to explain the user how his request has been attended and how the system proceeded to complete the tasks.
                //     """;

                String systemMessage = """
                    You provide a summary of the plan execution.

                    Based on the inputs, create a summary of the requests processed and their results.
                    Don't use Markdown, use HTML instead. Use bullet points to display the summary, as follows:
                        
                        <li>summary item</li>
                        <li>summary item</li>

                    When a result contains an iframe with an invoice URL, please format into a foldable HTML entity, as follows:

                        <details>
                            <summary style="color:blue">Click to show PDF</summary>
                            <iframe>url</iframe>
                        </details>

                    The intention is to explain the user how his request has been attended and how the system proceeded to complete the tasks.
                    """;


                String user = payload;
                messages.add(new SystemMessage(systemMessage));
                // messages.add(new UserMessage(payload));
                messages.add(new UserMessage(user));

                exchange.getIn().setBody(messages);
            }
        };
    }



    @BindToRegistry(lazy=true)
    public static Processor createAgentMessagePlanSummary(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                //reset activity
                activity = "";

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();


                String systemMessage = """
                    You provide a summary of the plan execution.

                    Based on the inputs, create a summary of the requests processed and their results.

                    Use the JSON pattern below to compose your response:
                        
                    {
                        "step 1": "summary",
                        "step 2": "summary",

                        (when the summary includes a list, or when relevant, nest data as follows)
                        "step3": [
                            "summary details 1",
                            "summary details 2",
                            "summary with 'details in quotes'" (do not use double quotes in string values)
                            "summary with new lines (\") are forbidden" (don't use \", use single quotes instead.)
                            "summary with new lines (\n) are forbidden" (don't use \n, use nested structures instead.)
                        ]                        
                    }

                    The intention is to explain the user how his request has been attended and how the system proceeded to complete the tasks.

                    Respond with raw JSON.
                    """;

                    // The JSON payload above will be processed by JavaScript to produce an HTML summary so it's very important you produce JSON data that is HTML friendly.
                    // Therefore, ensure rules like the ones that follow are taken in account:
                    // - Do not include carriage return characters (\n), use nested structures instead.
                    // - Do not include double quotes on the string values, nor escaped quote (\")
                    // - Do not include other characters that may clash with JavaScript or HTML syntax.
                    // - You can url encode strings containing not HTML friendly characters. 


                String user = payload;
                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(user));

                exchange.getIn().setBody(messages);
            }
        };
    }


    //Controller declaration
    public static class ActivityLogger {};

    @BindToRegistry
    public static ActivityLogger logger(){

        //Controller implementation
        return new ActivityLogger(){

            //Needs revision
            String activity = "";

            // public synchronized void init(CamelContext context) {

            // 	System.out.println("INIT LOGGER");

            // 	this.activity = "";
            // 	this.context = context;
            // }


            public void log(String log){
            	activity += "\n>" + log;
            	System.out.println("logging activity: "+log);
            }

            public void reset(){
            	activity = "";
            }

            public String getActivity(){
            	return activity;
            }


        };
    }

}
