package com.example.movieAssistant.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic createNewFriendshipTopic() {
        return TopicBuilder.name("new_friendship").build();
    }

    @Bean
    public NewTopic createWishCreateTopic() {
        return TopicBuilder.name("wish_create").build();
    }

    @Bean
    public NewTopic createWisUpdateTopic() {
        return TopicBuilder.name("wish_update").build();
    }
}
