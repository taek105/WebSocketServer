package wafs.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;
import wafs.chat.dto.ChatRequest;
import wafs.chat.dto.ChatResponse;
import wafs.chat.dto.ModerationResponse;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final KafkaTemplate<Long, String> kafkaTemplate;
    private int num = 1;

    private final ConcurrentMap<Long, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(KafkaTemplate<Long, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        Long roomId = Long.valueOf(
                UriComponentsBuilder.fromUri(session.getUri())
                        .build()
                        .getQueryParams()
                        .getFirst("roomId")
        );

        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet())
                .add(session);
        session.sendMessage(new TextMessage(
                "{\"type\":\"welcome\",\"nickname\":\"익명"+(num++)+"\"}"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        roomSessions.values().forEach(set -> set.remove(session));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        ChatRequest chat = mapper.readValue(message.getPayload(), ChatRequest.class);

        kafkaTemplate.send("chat", chat.roomId(), mapper.writeValueAsString(chat));
    }

    public void fanoutChat(ChatResponse msg) {
        Set<WebSocketSession> sessions = roomSessions.getOrDefault(msg.roomId(), Set.of());
        String json;
        try {
            json = mapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            log.error("serialize error", e);
            return;
        }
        sessions.forEach(s -> {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(json));
                } catch (IOException e) {
                    log.warn("send fail", e);
                }
            }
        });
    }

    public void fanoutModeration(ModerationResponse resp) {
        Set<WebSocketSession> sessions = roomSessions.getOrDefault(resp.roomId(), Set.of());
        String json;
        try {
            json = mapper.writeValueAsString(resp);
        } catch (JsonProcessingException e) {
            log.error("serialize error", e);
            return;
        }
        sessions.forEach(s -> {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(json));
                } catch (IOException e) {
                    log.warn("send fail", e);
                }
            }
        });
    }
}
