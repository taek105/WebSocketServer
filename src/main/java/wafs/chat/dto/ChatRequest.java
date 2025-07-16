package wafs.chat.dto;

public record ChatRequest(
        long roomId,
        String user,
        String content
) {

}
