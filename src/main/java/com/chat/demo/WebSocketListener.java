package com.chat.demo;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketListener {

    private final SimpMessageSendingOperations messagingTemplate = null;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WebSocketListener.class);
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = extractUsernameFromEvent(event);
        if (username != null) {
            logDisconnection(username);
            sendUserLeftMessage(username);
        }
    }

    private String extractUsernameFromEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        return (String) headerAccessor.getSessionAttributes().get("username");
    }

    private void logDisconnection(String username) {
        log.info("user disconnected: {}", username);
    }

    private void sendUserLeftMessage(String username) {
        ChatMessage chatMessage = createLeaveMessage(username);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    private ChatMessage createLeaveMessage(String username) {
        return ChatMessage.builder()
                .type(MessageType.LEAVER)
                .sender(username)
                .build();
    }
}
