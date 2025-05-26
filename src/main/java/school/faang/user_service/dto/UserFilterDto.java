package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record UserFilterDto (String namePattern,
                             String phonePattern,
                             Integer experienceMin,
                             Integer experienceMax) {}
