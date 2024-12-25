package school.faang.user_service.dto;

public record ResourceDocumentResponseDto(
        Long resourceId,
        String resourceType,
        Long ownerId
) {
}
