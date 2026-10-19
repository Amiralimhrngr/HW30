package ir.maktabsharif.dto;

import java.time.LocalDateTime;

public record PostRespDto(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
