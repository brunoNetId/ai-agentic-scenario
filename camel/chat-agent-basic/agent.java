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

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


public class agent extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Routes are loaded from YAML files
    }

    private static String LLM_URL;

    @PropertyInject("ollama.llm.url")
    public void setLlmUrl(String url) {

        System.out.println("someone is setting the url: "+url);

        // try {
        //     throw new Exception("show trace");
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     // TODO: handle exception
        // }

        LLM_URL = url;
    }

    public static String getLlmUrl() {
        return LLM_URL;
    }

    @BindToRegistry(lazy=true)
    public static ChatLanguageModel chatModelMain(){

        System.out.println("the url to use:" + getLlmUrl());

        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey("EMPTY")
            // .modelName("qwen2.5:0.5b-instruct")
            // .modelName("qwen2.5:3b-instruct")
            .modelName("qwen2.5:7b-instruct")
            // .modelName("qwen2.5:14b-instruct")
            // .modelName("granite3.2:2b")
            // .modelName("granite3.2:8b")
            .baseUrl("http://"+getLlmUrl()+"/v1/")
            .temperature(0.0)
            .timeout(ofSeconds(180))
            .logRequests(true)
            .logResponses(true)
            .build();

        return model;
    }

    @BindToRegistry(lazy=true)
    public static Processor createChatMessage(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();

                // String systemMessage = """
                //     You are a helpul customer support assistant.

                //     You will assist in handling operations related to invoices, promotions, notifications.

                //     %s

                //     Respond with short answers.

                //     When providing links to invoices always use the format:
                //     <iframe src="url to resource" width="100%%" height="680"></iframe>
                //     """;

                    String systemMessage = """
                        You are a helpul customer support assistant.
    
                        You will assist in handling operations related to invoices, promotions, notifications.
    
                        %s
    
                        Respond with short answers.
    
                        Only when the response of a tool call provides an URL, wrap it as follows:
                        <iframe src="url to resource" width="100%%" height="680"></iframe>
                        """;

                String tools = """
                    You have access to a collection of tools.

                    You can use multiple tools at the same time.

                    When providing information about promotions, always include its ID.

                    When a tool provides an error status, try to recover as per the details provided.

                    Do not make duplicate tool calls.

                    Complete your answer using data obtained from the tools.
                    """;


                    // Use short answers.


                messages.add(new SystemMessage(systemMessage.formatted(tools)));
                messages.add(new UserMessage(payload));

                exchange.getIn().setBody(messages);
            }
        };
    }

}
