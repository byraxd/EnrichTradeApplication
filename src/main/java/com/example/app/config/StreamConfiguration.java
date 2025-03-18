package com.example.app.config;

import org.springframework.amqp.core.Queue;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@Configuration
@EnableBinding(Source.class)
public class StreamConfiguration {

    @Bean
    public MessageChannel messageChannel() {
        DirectChannel dc = new DirectChannel();
        dc.subscribe(messageHandler());
        return dc;
    }

    @Bean
    public MessageHandler messageHandler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println("Received message: " + message.getPayload());
            }
        };
    }

    @Bean
    public Queue queue() {
        return new Queue("my-queue", false);
    }
}
