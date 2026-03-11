package com.hudi.spingai.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * @author hudi
 * @date 11 3月 2026 14:19
 */
@SpringBootTest
public class OllamaTest {


    @Test
    public void test1(@Autowired OllamaChatModel chatModel) {
        OllamaChatOptions options = OllamaChatOptions.builder()
                .disableThinking()
                .build();
        ChatResponse call = chatModel.call(new Prompt("你好，你是谁", options));
        call.getResult().getOutput().getText();
    }

    @Test
    public void stream(@Autowired OllamaChatModel chatModel) {
        OllamaChatOptions options = OllamaChatOptions.builder()
                .disableThinking()
                .build();
        Flux<ChatResponse> stream = chatModel.stream(new Prompt("你好，你是谁", options));
        stream.doOnNext(i -> System.out.println(i.getResult().getOutput().getText()));
    }

}
