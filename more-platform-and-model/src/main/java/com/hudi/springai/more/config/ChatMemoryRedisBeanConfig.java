package com.hudi.springai.more.config;



import com.alibaba.cloud.ai.memory.redis.JedisRedisChatMemoryRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hudi
 * @date 19 3月 2026 15:45
 * 想使用redis必须配置该类，否则redis不生效
 */
@Configuration
public class ChatMemoryRedisBeanConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.password}")
    private String password;
    @Value("${spring.data.redis.database}")
    private int database;


    @Bean
    public JedisRedisChatMemoryRepository jedisRedisChatMemoryRepository() {

        JedisRedisChatMemoryRepository build = JedisRedisChatMemoryRepository.builder()
                .host(host)
                .port(port)
                .password(password)
                .database(database)
                .build();

        return build;
    }

    @Bean
    public ChatMemory chatMemory(JedisRedisChatMemoryRepository jedisRedisChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .chatMemoryRepository(jedisRedisChatMemoryRepository)
                .build();
    }
}
