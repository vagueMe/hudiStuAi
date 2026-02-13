package com.hudi.spingai.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * @author hudi
 * @date 13 2月 2026 15:06
 */
@SpringBootTest
public class TestDeepseek {



    @Test
    public void testDeepseek(@Autowired DeepSeekChatModel chatModel) {
        ChatResponse call = chatModel.call(new Prompt("你好，你是谁"));
        DeepSeekAssistantMessage message = (DeepSeekAssistantMessage) call.getResult().getOutput();
        System.out.println("tinkMsg = " + message.getReasoningContent());
        System.out.println("\t");
        System.out.println("call = " + message.getText());

    }

    @Test
    public void testDeepseekStream(@Autowired DeepSeekChatModel chatModel) {
//        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
//               .maxTokens().stop().temperature()
//                .build();
//        Prompt prompt = new Prompt("你好，你是谁", options);
        Prompt prompt = new Prompt("你好，你是谁");
        Flux<ChatResponse> stream = chatModel.stream(prompt);
        stream.toIterable().forEach(i-> System.out.println(i.getResult().getOutput().getText()));
    }
}
