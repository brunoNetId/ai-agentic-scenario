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


public class card extends RouteBuilder {

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
    @PropertyInject("model.card.name")
    public void setModelName(String name) {
        MODEL_NAME = name;
    }
    public static String getModelName() {
        return MODEL_NAME;
    }


    @BindToRegistry(lazy=true)
    public static ChatLanguageModel chatModelCard(){

        System.out.println("the url to use:" + getLlmUrl());

        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey("EMPTY")
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
    public static Processor requestCardMessage(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                List<ChatMessage> messages = new ArrayList<>();

                String systemMessage = """
                    You are an assistant to provide information related to the tools or functions you have available.

                    Do not call any tools or functions.

                    Only answer questions related to the tools or functions available to you.

                    Always provide an answer using the following JSON format:

                        {
                            "tools": [
                              {
                                "description": its description,
                                "purpose": its purpose,
                                "parameters": [
                                  {"name": name of 1st parameter, "type": its type},
                                  {"name": name of 2nd parameter, "type": its type},

                                  other parameters (when relevant)
                                ]
                              },

                              more tools (where there are more)
                            ]
                        }

                    Here is an example output:

                        {
                            "tools": [
                              {
                                "description": "Award a promotion to a customer",
                                "purpose": "Assign a promotional offer to a specific customer",
                                "parameters": [
                                  {"name": "invoiceid", "type": "number"},
                                  {"name": "promotionid", "type": "number"}
                                ]
                              }
                            ]
                        }

                    When defining parameters ensure types align with valid JSON schema types: array, boolean, integer, number, null, object, or string

                    Respond with the raw JSON data only.

                    For any other unrelated questions respond with something similar to:

                        "I'm sorry, I only provide information about the tools or functions that I have available."

                    """;

                // We force the user message to obtain the list of tools available
                String userMessage = "please list the tools you have available";

                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(userMessage));

                exchange.getIn().setBody(messages);
            }
        };
    }
}
