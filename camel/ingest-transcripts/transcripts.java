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
            // .modelName("qwen2.5:7b-instruct")
            .modelName("qwen2.5:14b-instruct")
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
    public static Processor createSummary(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();


                String systemMessage = """
                    You are an assistant to help summarizing conversations and plan problem resolutions.


                    To generate the resolution plan to solve the customer's problem, use concise instructions that are direct, assertive and firm to make as short and clear as possible.

                    You have tools available to obtain and include the types of products the customer is interested in based on the invoice details.

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

                    Do not exclude crucial information needed by the executor, the field 'resolution' should contain all the relevant information to execute the task.
                    
                    Do not include resolution entries performing verification tasks similar to "Verify and correct...".

                    Design the resolution prompts to match the following capabilities an assistan will use to process them:
                    - amend invoices according to given instructions
                    - award promotions to customers based their invoice id and their interests

                    Inspect the history of messages and ensure that you don't duplicate "tool_calls" for the assistant role.
                    Make sure the function 'extractFromInvoiceCategoriesOfProductsCustomerIsInterestedIn' is only call once and no more.

                    Do not make duplicate tool calls.

                    Respond with raw JSON data.
                    """;


                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(payload));

                exchange.getIn().setBody(messages);
            }
        };
    }
}
