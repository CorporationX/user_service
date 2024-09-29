package school.faang.user_service.service.filter.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;

public record RequestFilterDto(RequestStatus status, String messagePattern) {}
