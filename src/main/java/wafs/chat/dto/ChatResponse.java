package wafs.chat.dto;

public record ChatResponse(
        long roomId,
        long messageId,
        String user,
        String content
) {

}
