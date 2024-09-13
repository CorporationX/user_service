package school.faang.user_service.service;

import school.faang.user_service.dto.SkillDto;

import java.util.List;

public interface SkillServiceMethodsForEventService {
    List<SkillDto> getUserSkillDtoList(Long userId);
}
