package wafs.chat.dto;

public record ModerationResponse(
        long roomId,
        long messageId,
        boolean isSafe,
        String reason
) {

}
