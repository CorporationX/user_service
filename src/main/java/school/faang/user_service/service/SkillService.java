package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserService userService;

    public List<SkillDto> getUserSkillsList(Long userId) {
        userService.getUser(userId);

        List<Skill> ownerSkillsList = skillRepository.findAllByUserId(userId);
        return skillMapper.toDtoList(ownerSkillsList);
    }
}
