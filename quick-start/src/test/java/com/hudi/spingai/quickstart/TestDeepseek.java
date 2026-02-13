package com.hudi.spingai.quickstart;

import org.junit.jupiter.api.Test;
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
        String call = chatModel.call("你好，你是谁");
        System.out.println("call = " + call);
    }

    @Test
    public void testDeepseekStream(@Autowired DeepSeekChatModel chatModel) {
        Flux<String> stream = chatModel.stream("你好，你是谁");
        stream.toIterable().forEach(System.out::println);
    }
}
