package com.hudi.spingai.quickstart;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author hudi
 * @date 11 3月 2026 14:43
 */
@SpringBootTest
public class MoreTest {




    @Test
    public void  testOnlyOne(@Autowired
                          ChatClient.Builder chatClientBuilder) {
       chatClientBuilder.build().prompt("你好，你是谁").call();
    }


    @Test
    public void testMore(@Autowired DeepSeekChatModel chatModel) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        String content = builder.build().prompt("你好，你是谁").call().content();
        System.out.println("content = " + content);
    }

    @Test
    public void testMore(@Autowired DashScopeChatModel chatModel) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        String content = builder.build().prompt("你好，你是谁").call().content();
        System.out.println("content = " + content);
    }
}
