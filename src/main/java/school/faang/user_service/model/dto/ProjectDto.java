package school.faang.user_service.model.dto;

import lombok.Builder;

@Builder
public record ProjectDto(
        long projectId,
        long ownerId
) {
}
