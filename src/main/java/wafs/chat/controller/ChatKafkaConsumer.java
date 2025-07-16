package wafs.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import wafs.chat.dto.ChatRequest;
import wafs.chat.dto.ChatResponse;
import wafs.chat.dto.ModerationResponse;


@Slf4j
@Component
public class ChatKafkaConsumer {

    private final ChatWebSocketHandler chatHandler;
    private final ObjectMapper mapper = new ObjectMapper();

    public ChatKafkaConsumer(ChatWebSocketHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @KafkaListener(topics = "chat", containerFactory = "kafkaListenerFactory")
    public void listenChat(ConsumerRecord<Long, String> record) {
        try {
            ChatRequest raw = mapper.readValue(record.value(), ChatRequest.class);

            long offset = record.offset();
            ChatResponse chat = new ChatResponse(
                    raw.roomId(),
                    offset,
                    raw.user(),
                    raw.content()
            );

            System.out.println("chat" + chat);
            chatHandler.fanoutChat(chat);
        } catch (Exception e) {
            log.error("Kafka chat msg parse error", e);
        }
    }

    @KafkaListener(topics = "moderation", containerFactory = "kafkaListenerFactory")
    public void listenModeration(ConsumerRecord<Long, String> record) {
        try {
            ModerationResponse response = mapper.readValue(record.value(), ModerationResponse.class);
            System.out.println("moderation" + response);
            chatHandler.fanoutModeration(response);
        } catch (Exception e) {
            log.error("Kafka msg parse error", e);
        }
    }
}
