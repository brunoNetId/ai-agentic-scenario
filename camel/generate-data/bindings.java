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

public class bindings extends RouteBuilder {

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
            .baseUrl("http://"+getLlmUrl()+"/v1/")
            .temperature(0.0)
            .timeout(ofSeconds(180))
            .logRequests(true)
            .logResponses(true)
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

                String systemMessage = """
                    You are an assistant to help summarizing conversations and plan resolutions.

                    Read the user message and perform the 2 tasks detailed below.

                    The first task is to sumarize in one sentence the problem the customer has.

                    The second task simulates a support agent taking care of the customer's problem.
                    Compose a chat message that pretends to be the agent asking an LLM bot assistant to assist.
                    The message should include concise instructions the bot can use to correct the customer's problem.
                    The message should be direct, assertive and firm to make as short and clear as possible.
                    The bot will have the capabilities listed below:
                    - review cases given the invoice ID and information about the conversation
                    - inspect the types of products purchased by the customer
                    - obtain information about the promotion programs currently active in the organisation
                    - send a promotion to a customer
                    - send notifications to the customer

                    An example for task one and two would be:

                    ### Summary of the Problem:
                    The customer noticed an issue with their invoice total, suspecting a mistake in the net worth calculation for one of the items.

                    ### Support Resolution Message:
                    Ask the case revision team to inspect and validate the customers claim for invoice 49565075.
                    If the problem is aknowledged, solve it and send the customer some vouchers for products he may be interested in.
                    """;



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
