package com.hudi.springai.more.controller;

import com.hudi.springai.more.dt.AiJob;
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
@RequestMapping("clients")
public class MoreClientController {



    @Autowired
    private ChatClient planningChatClient;

    @Autowired
    private ChatClient botChatClient;


    @GetMapping(value = "/stream", produces = "text/stream;charset=UTF-8")
    public Flux<String> stream(@RequestParam(defaultValue = "你好，你是谁") String message,
                               @RequestParam(defaultValue = "1") String id) {


        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        sink.tryEmitNext("正在计划任务...<br/>");

        new Thread(() -> {
            AiJob.Job job = planningChatClient.prompt().advisors( i -> i.param(ChatMemory.CONVERSATION_ID,id)).user(message)
                    .call().entity(AiJob.Job.class);
            System.out.println(job);
            if (job != null) {
                switch (job.jobType()) {
                    case CANCEL -> {
                        if (job.keyInfos().isEmpty()) {
                            sink.tryEmitNext("请输入姓名和订单号。");
                        } else {
                            // 执行业务逻辑
                            sink.tryEmitNext("退票成功！");
                        }
                    }
                    case QUERY -> {
                        if (job.keyInfos().isEmpty()) {
                            sink.tryEmitNext("请输入订单号.");
                        } else {
                            // 执行业务 查询
                            sink.tryEmitNext("查询预定信息：xxx！");
                        }

                    }case OTHER -> {
                        botChatClient.prompt()
                                .advisors(i -> i.param(ChatMemory.CONVERSATION_ID,id))
                                .user(message)
                                .stream().content().doOnNext(sink::tryEmitNext)
                                .doOnComplete(sink::tryEmitComplete)
                                .subscribe();
                    }
                    default -> {
                        System.out.println(job);
                        sink.tryEmitNext("解析失败");
                    }
                }
            } else {
                sink.tryEmitNext("解析失败1");
            }
        }).start();

        return sink.asFlux();
    }



}
