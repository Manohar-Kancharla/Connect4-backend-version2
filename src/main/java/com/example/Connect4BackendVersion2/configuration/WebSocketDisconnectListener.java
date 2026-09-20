package com.example.Connect4BackendVersion2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.Connect4BackendVersion2.service.Connect4Service;

@Component
public class WebSocketDisconnectListener {

    @Autowired
    private Connect4Service connect4Service;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        String sessionId = event.getSessionId();

        try {
            connect4Service.disconnect(sessionId);
        } catch (Exception e) {
            // Session was not associated with any active game.
        }
    }
}