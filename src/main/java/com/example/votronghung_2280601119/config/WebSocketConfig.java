package com.example.votronghung_2280601119.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Khai báo một địa chỉ /ws để Frontend (JS) kết nối vào
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Cấu hình tiền tố cho tin nhắn.
        // Khi Server gửi tin nhắn xuống người dùng thì sẽ dùng tiền tố /topic
        registry.enableSimpleBroker("/topic");

        // Khi Frontend (JS) bắn tin nhắn lên Server thì dùng tiền tố /app
        registry.setApplicationDestinationPrefixes("/app");
    }
}