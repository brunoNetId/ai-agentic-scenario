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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class graph extends RouteBuilder {

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
    public static ChatLanguageModel chatModelGraphs(){

        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey("EMPTY")
            // .modelName("qwen2.5:3b-instruct")
            // .modelName("qwen2.5:7b-instruct")
            .modelName("qwen2.5:14b-instruct")
            
            // Qwen3 (4b,8b,14b) does not seem to perform better than 2.5-instruct for Mermaid
            // .modelName("qwen3:8b")

            // .baseUrl("http://"+getLlmUrl()+"/v1/")
            .baseUrl("http://"+getLlmUrl()+"/v1/")
            .temperature(0.0)
            .timeout(ofSeconds(180))
            .logRequests(true)
            .logResponses(true)
            .build();

        return model;
    }

    @BindToRegistry(lazy=true)
    public static Processor composeGraph(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);
                List<ChatMessage> messages = new ArrayList<>();


                String systemMessage = """
                    /no_think

                    You are an assistant to help create Mermaid flow charts.

                    You will be given one or more arrays of JSON, like [0] [1] ... [N]

                    Each array describes interactions. Combine the arrays and organise them as steps in the following manner:

                        ---
                        config:
                            flowchart:
                                subGraphTitleMargin:
                                    bottom: 40    
                        ---
                        graph LR
                            A[Start] -- Step 0 --> B
                        
                            subgraph B[description]
                                direction TB
                                B1[Tool call]
                                B2[Tool response]
                                B1 --> B2
                            end

                            subgraph C[description]
                                direction TB
                                C1[Tool call]
                                C2[Tool response]
                                C1 --> C2
                            end

                            B -- Step 1 --> C
                            C --> D[End]

                    When a step contains multiple groups with multiple tool calls, for example:

                        {
                        "group" : [ {
                            "tool" : {
                            "id" : id,
                            "name" : name,
                            "response" : response
                            }
                        }, {
                            "tool" : {
                            "id" : id,
                            "name" : name,
                            "response" : response
                            }
                        } ]
                        }

                    then combine all the tool calls of the same group in a single subgraph as follows:

                        subgraph D[description]
                            direction TB
                            subgraph DS1[Group calls 1]
                                direction TB
                                D1[Tool call]
                                D2[Tool response]
                                D1 --> D2

                                direction TB
                                D3[Tool call]
                                D4[Tool response]
                                D3 --> D4
                            end

                            subgraph DS2[Group calls 2]
                                direction TB
                                D5[Tool call]
                                D6[Tool response]
                                D5 --> D6
                            end

                            DS1 --> DS2
                        end


                    An interaction may have no tool calls at all, for example:

                    graph TD
                        A[hello] --> B[Assistant Response: 'Hello! How can I assist you today?']
                        B --> C[End]

                    Respond with the raw Mermaid code.
                    Do not include the Markdown notation wrapper with backticks.
                    """;


                messages.add(new SystemMessage(systemMessage));
                messages.add(new UserMessage(payload));

                exchange.getIn().setBody(messages);
            }
        };
    }

    @BindToRegistry(lazy=true)
    public static Processor findInvoiceId(){

        return new Processor() {
            public void process(Exchange exchange) throws Exception {

                String payload = exchange.getMessage().getBody(String.class);

                String regex = "invoiceid:\\s*(\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(payload);

                matcher.find();

                String id = "undefined";
                try{
                    id = matcher.group(1);
                }
                catch(Exception e){
                    //could not obtain an ID
                }

                exchange.setVariable("invoiceid", id);
            }
        };
    }

}
