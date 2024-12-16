package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record ProjectDto(
    long projectId,
    long ownerId
) {
}
