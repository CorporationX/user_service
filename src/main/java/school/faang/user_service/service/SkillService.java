package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.mapper.SkillCustomMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService implements SkillServiceMethodsForEventService {
    private final SkillRepository skillRepository;
    private final SkillCustomMapper skillMapper;

    @Override
    public List<SkillDto> getUserSkillDtoList(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toDto)
                .toList();
    }
}
