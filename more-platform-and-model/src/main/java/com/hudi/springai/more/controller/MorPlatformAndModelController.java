package com.hudi.springai.more.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.hudi.springai.more.opts.MorePlatformAndModelOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@RestController
public class MorPlatformAndModelController {

    HashMap<String, ChatModel> platforms=new HashMap<>();

    public MorPlatformAndModelController(
            DashScopeChatModel dashScopeChatModel,
            DeepSeekChatModel deepSeekChatModel,
            OllamaChatModel ollamaChatModel

    ) {
        platforms.put("dashscope", dashScopeChatModel);
        platforms.put("ollama", ollamaChatModel);
        platforms.put("deepseek", deepSeekChatModel);
    }

    @Value("classpath:files/prompt.st")
    private Resource resource;

    @RequestMapping(value="/chat",produces = "text/stream;charset=UTF-8")
    public Flux<String> chat(
            @RequestParam(defaultValue = "你好，你是谁") String message,
            @RequestParam(defaultValue = "dashscope")  String platform){
        ChatModel chatModel = platforms.get(platform);
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        ChatOptions options = ChatOptions.builder()
                .build();
        builder.defaultSystem(resource);// 预设角色
        builder.defaultOptions(options);
        ChatClient chatClient = builder.build();
        Flux<String> content = chatClient.prompt()
//                .system()
                .user(message).stream().content();
        return content;

    }
}
