package com.hudi.spingai.quickstart;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;

import java.net.MalformedURLException;

/**
 * @author hudi
 * @date 13 2月 2026 15:06
 */
@SpringBootTest
public class TestAll {


    @Test
    public void testDeepseekStream(@Autowired DashScopeChatModel chatModel) {
//        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
//               .maxTokens().stop().temperature()
//                .build();
//        Prompt prompt = new Prompt("你好，你是谁", options);
        Prompt prompt = new Prompt("你好，你是谁");
        ChatResponse call = chatModel.call(prompt);
        System.out.println(call.getResult().getOutput().getText());

    }

    @Test
    public void testMultimodal(@Autowired DashScopeChatModel dashScopeChatModel
    ) throws MalformedURLException {
        // flac、mp3、mp4、mpeg、mpga、m4a、ogg、wav 或 webm。
        var audioFile = new ClassPathResource("/files/1B8F54A5EC975DCD9C7589630FB04552.jpg");

        Media media = new Media(MimeTypeUtils.IMAGE_JPEG, audioFile);
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withMultiModel(true)
                .withModel("qwen3.5-plus").build();

        Prompt prompt = Prompt.builder().chatOptions(options)
                .messages(UserMessage.builder().media(media)
                        .text("识别图片").build())
                .build();
        ChatResponse response = dashScopeChatModel.call(prompt);

        System.out.println(response.getResult().getOutput().getText());
    }

}
