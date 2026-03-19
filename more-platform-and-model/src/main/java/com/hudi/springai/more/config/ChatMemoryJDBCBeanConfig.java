package com.hudi.springai.more.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
//import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hudi
 * @date 19 3月 2026 15:45
 * 在不配置该配置类时，如果引入了spring-ai-jdbc-chat-memory-repository，则也会默认使用jdbc的方式。
 * 配置该类，只是可以对数据库的方式进行参数优化。
 */
//@Configuration
public class ChatMemoryJDBCBeanConfig {

//    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .chatMemoryRepository(repository)
                .build();
    }
}
