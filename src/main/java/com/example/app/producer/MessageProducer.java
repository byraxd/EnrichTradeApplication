package com.example.app.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageProducer {

    @Autowired
    private MessageChannel messageChannel;

    @Value("200")
    private long timeoutMillis;

    public void send(String message){
        try {
            boolean isSent = messageChannel.send(new GenericMessage<>(message), timeoutMillis);

            if(isSent){
                System.out.println("Message was sent successfully: " + message);
            }else {
                System.out.println("Message " + message + " was not sent");
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
