package school.faang.user_service.dto;

import school.faang.user_service.entity.RequestStatus;

public record RequestFilterDto(RequestStatus status, String messagePattern) {}
