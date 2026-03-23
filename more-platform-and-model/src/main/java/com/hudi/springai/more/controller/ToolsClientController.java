package com.hudi.springai.more.controller;

import com.hudi.springai.more.service.ToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("tools")
public class ToolsClientController {


    @Autowired
    private ChatClient planningChatClient;

    @Autowired
    private ToolService toolService;


    @GetMapping(value = "/stream", produces = "text/stream;charset=UTF-8")
    public Flux<String> stream(@RequestParam(defaultValue = "你好，你是谁") String message,
                               @RequestParam(defaultValue = "1") String id) {


        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
//        sink.tryEmitNext("正在计划任务...<br/>");
        planningChatClient.prompt().user(message)
                .advisors(i -> i.param(ChatMemory.CONVERSATION_ID, id))
                .tools(toolService)
                .stream().content().doOnNext(sink::tryEmitNext)
                .doOnComplete(sink::tryEmitComplete)
                .subscribe();
        return sink.asFlux();
    }


}
