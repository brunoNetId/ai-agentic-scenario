//DEPS dev.langchain4j:langchain4j-open-ai:0.33.0
//DEPS com.github.javafaker:javafaker:1.0.2

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

import com.github.javafaker.Faker;

public class transcripts extends RouteBuilder {

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
    public static ChatLanguageModel chatModel(){

        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey("EMPTY")
            // .modelName("qwen2.5:3b-instruct")
            .modelName("qwen2.5:7b-instruct")
            // .modelName("qwen2.5:14b-instruct")
            .baseUrl("http://"+getLlmUrl()+"/v1/")
            .temperature(0.0)
            .timeout(ofSeconds(180))
            .logRequests(true)
            .logResponses(true)
            // .responseFormat("JSON")
            .build();

        return model;
    }

    @BindToRegistry(lazy=true)
    public static Processor createCustomerInteraction(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();

                // String systemMessage = """
                //     You are an assistant to help drafting simulated conversations.

                //     Read the user message, and simulate a conversation between a customer and a support agent.
                //     The interaction is started by the customer that has a complaint of some sort in relation to the content of his invoice.
                //     Make sure the agent asks for the invoice ID to use as a reference number.
                //     The agent attends the customer and communicates the problem will be looked at.

                //     The agent closes the conversation promising the customer he will be contacted soon with a resolution.
                //     Make the conversation not too long.                 
                //     """;

                    String systemMessage = """
                        You are an assistant to help drafting simulated customer/agent conversations.
    
                        Read the user message, and simulate a conversation between a customer and a support agent.
                        The customer initiates the conversation with a complaint of some sort in relation to the content of his invoice.
                        Make sure the agent asks during the conversation for the invoice ID to use as a reference number.
                        The agent attends the customer and explains the problem will be looked at.
    
                        The agent closes the conversation letting the customer know he/she will be contacted soon with a resolution.
                        Make the conversation is not too long.
                        """;
                        // Do not call the tool 'extractFromInvoiceCategoriesOfProductsCustomerIsInterestedIn' more than once.


                        // You have tools available but be careful, do not loop over the same tool calls to avoid infinite tool calling.


                        // **Support Agent:** Good day! Thank you for contacting our support team. I understand you have a concern regarding your invoice. Could you please provide me with the invoice ID so I can look into this further? It will help us resolve the issue more efficiently.

                        // **Customer:** Sure, my invoice ID is 74184749.

                // messages.add(new SystemMessage(systemMessage.formatted(tools)));
                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(payload));

                exchange.getIn().setBody(messages);
            }
        };
    }



    @BindToRegistry(lazy=true)
    public static Processor createSummary(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();


                // String systemMessage = """
                //     You are an assistant to help summarizing conversations and plan resolutions.

                //     Read the user message and perform the 2 tasks detailed below.

                //     The first task is to sumarize in one sentence the problem the customer has.

                //     The second task is to generate a prompt containing a resolution plan to solve the customer's problem.
                //     It should include concise instructions that are direct, assertive and firm to make as short and clear as possible.
                //     Do not include resolution tasks like "Verify and correct...".

                //     Also include the types of products the customer is interested in based on the invoice details.

                //     Follow the JSON pattern below to provide your answer:

                //     {
                //         "invoiceid": <invoice_number>
                //         "interests": product categories the customer is interested in
                //         "summary":"The customer suspects an error in their invoice total for Invoice no: <invoice_number>, believing it should be $80 instead of $87.96."
                //         "resolution":[
                //             "Fix the invoice <invoice_number>, the total amount should be $80 instead of $87.96.",
                //             "Get the active promotions and award a promotion that fits the customer product interest""
                //         ]
                //     }
                    
                //     Design the prompt taking in consideration it'll be processed by an assistant (not you) with the following capabilities:
                //     - amend invoices according to given instructions
                //     - obtain current active promotion programs
                //     - award promotions to customers

                //     Do not make duplicate tool calls.

                //     Respond with raw JSON data.
                //     """;


                String systemMessage = """
                    You are an assistant to help summarizing conversations and plan resolutions.

                    Read the user message and perform the 2 tasks detailed below.

                    The first task is to sumarize in one sentence the problem the customer has.

                    The second task is to generate a prompt containing a resolution plan to solve the customer's problem.
                    It should include concise instructions that are direct, assertive and firm to make as short and clear as possible.
                    
                    Also include the types of products the customer is interested in based on the invoice details.

                    Follow the JSON pattern below to provide your answer:

                    {
                        "invoiceid": <invoice_number>
                        "interests": product categories the customer is interested in
                        "summary":"The customer suspects an error in their invoice total for Invoice no: <invoice_number>, believing it should be $80 instead of $87.96."
                        "resolution":[
                            "Fix the invoice <invoice_number>, the total amount should be $80 instead of $87.96.",
                            "Get the active promotions and award a promotion that fits the customer product interest""
                        ]
                    }
                    
                    Do not include resolution entries performing verification tasks similar to "Verify and correct...".

                    Design the resolution prompts to match the following capabilities an assistan will use to process them:
                    - amend invoices according to given instructions
                    - obtain details of promotion programs available, including id and description for each promotion available
                    - award promotions to customers based on an invoice id

                    Inspect the history of messages and ensure that you don't duplicate "tool_calls" for the assistant role.
                    Make sure the function 'extractFromInvoiceCategoriesOfProductsCustomerIsInterestedIn' is only call once and no more.

                    Respond with raw JSON data.
                    """;

                    // You have tools available but be careful, do not loop over the same tool calls to avoid infinite tool calling.


                    // Do not make duplicate tool calls.


                    // Design the prompt taking in consideration it's be processed by an assistant (not you) with the following capabilities:
                    // - amendInvoiceWithGivenInstructions(invoiceid,message)
                    // - getActivePromotions()
                    // - awardsAPromotionToACustomer(invoiceid,promotionid)
                    // - FindsCustomersInterestedInATypeOfProduct(productType)



                    // ### Summary of the Problem:
                    // The customer noticed an issue with their invoice total, suspecting a mistake in the net worth calculation for one of the items.

                    // ### Support Resolution Message:
                    // Ask the case revision team to inspect and validate the customers claim for invoice 49565075.
                    // If the problem is aknowledged, solve it and send the customer some vouchers for products he may be interested in.



                    // The second task is to compose a set of resolution actions. Compose the list using natural language but taking in account the list of actions will
                    // be processed by an autonomous agent that will have the following tools available:
                    // - reviewCase(invoiceNumber, conversation)
                    // - getProductTypes(invoiceNumber)
                    // - getListActivePromotions()
                    // - sendPromotionToCustomer()
                    // - notifyCustomer(invoiceNumber)

                // messages.add(new SystemMessage(systemMessage.formatted(tools)));
                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(payload));

                exchange.getIn().setBody(messages);
            }
        };
    }


}
