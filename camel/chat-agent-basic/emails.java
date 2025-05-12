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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class emails extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Routes are loaded from YAML files
    }


    //Model URL configuration
    private static String LLM_URL;
    @PropertyInject("llm.url")
    public void setLlmUrl(String url) {
        LLM_URL = url;
    }
    public static String getLlmUrl() {
        return LLM_URL;
    }

    //Model name configuration
    private static String MODEL_NAME;
    @PropertyInject("model.emails.name")
    public void setModelName(String name) {
        MODEL_NAME = name;
    }
    public static String getModelName() {
        return MODEL_NAME;
    }


    @BindToRegistry(lazy=true)
    public static ChatLanguageModel chatModelEmails(){

        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey("EMPTY")
            // .modelName("qwen2.5:3b-instruct")
            // .modelName("qwen2.5:7b-instruct")
            // .modelName("qwen2.5:14b-instruct")
            .modelName(getModelName())
            .baseUrl("http://"+getLlmUrl()+"/v1/")
            .temperature(0.0)
            .timeout(ofSeconds(180))
            .logRequests(true)
            .logResponses(true)
            .build();

        return model;
    }

    @BindToRegistry(lazy=true)
    public static Processor composeEmail(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();

                String systemMessage = """
                    You are an assistant to help compose email communications to customers.

                    The user message will be formatted as follows:

                    {
                        "prompt": some prompt,
                        "input-data":{
                            some input data
                        }
                    }

                    From the user message data, follow the prompt instructions using the input data to compose an email notification.
                    
                    The email should conform to the following pattern:

                        to: email address
                        subject: the subject

                        Salutation content.

                        Message following the prompt instructions using the input data.

                        Complimentary closing content.

                        Signature content: Globex ltd.
                    """;

                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(payload));

                exchange.getIn().setBody(messages);
            }
        };
    }
}
