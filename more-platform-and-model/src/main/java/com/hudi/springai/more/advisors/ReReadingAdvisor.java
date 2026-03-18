package com.hudi.springai.more.advisors;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * @author hudi
 * @date 12 3月 2026 10:39
 */
public class ReReadingAdvisor implements BaseAdvisor {

    private String beforText = "" +
            "今天天气怎么样。{change}";
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String  meg = chatClientRequest.prompt().getUserMessage().getText();
        String change = PromptTemplate.builder().template(beforText).build().render(Map.of("change", meg));
//        ChatClientRequest build = chatClientRequest.mutate().prompt(Prompt.builder().content(change).build()).build();
        ChatClientRequest build = chatClientRequest.mutate().prompt(new Prompt(change)).build();
        return build;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {

        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
