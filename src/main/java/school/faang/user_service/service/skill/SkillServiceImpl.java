package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;
    private final UserContext userContext;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        skillValidator.validateCreate(skillDto);

        Skill skill = skillMapper.toSkill(skillDto);
        Skill savedSkill = skillRepository.save(skill);

        return skillMapper.toSkillDto(savedSkill);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        userId = userContext.getUserId();
        List<Skill> userSkills = skillRepository.findAllByUserId(userId);

        return skillMapper.toSkillDtoList(userSkills);
    }
}
