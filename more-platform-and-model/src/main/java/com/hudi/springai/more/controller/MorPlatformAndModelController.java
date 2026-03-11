package com.hudi.springai.more.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.hudi.springai.more.opts.MorePlatformAndModelOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
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
import java.util.Map;

@RestController
public class MorPlatformAndModelController {

    HashMap<String, ChatModel> platforms=new HashMap<>();

    private Map<String , ChatClient> chatClientMap;

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
        ChatClient chatClient = this.initChatClient(platform);
       return chatClient.prompt()
//                .system()
                .user(message).stream().content();

    }

    public ChatClient initChatClient(String platform) {
        if (chatClientMap == null || chatClientMap.get(platform) == null) {
            if (chatClientMap == null) {
                chatClientMap = new HashMap<>();
            }
            ChatModel chatModel = platforms.get(platform);
            if (chatModel == null) {
                throw new RuntimeException("未找到对应的平台");
            }
            ChatClient.Builder builder = ChatClient.builder(chatModel);
            ChatOptions options = ChatOptions.builder()
                    .build();
            builder.defaultSystem(resource);// 预设角色
            builder.defaultOptions(options);
            builder.defaultAdvisors(new SimpleLoggerAdvisor());
            ChatClient chatClient = builder.build();
            chatClientMap.put(platform, chatClient);
            return chatClient;
        } else {
            return chatClientMap.get(platform);
        }
    }

}
