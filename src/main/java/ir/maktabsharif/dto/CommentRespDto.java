package ir.maktabsharif.dto;

import java.time.LocalDateTime;

public record CommentRespDto(
        Long id,
        String content,
        Long authorId,
        String authorUsername,
        Long postId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
