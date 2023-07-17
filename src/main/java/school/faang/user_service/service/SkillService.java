package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserService userService;

    public List<SkillDto> getUserSkills(long userId) {
        userService.checkUserById(userId);
        List<Skill> skillsOfUsers = skillRepository.findAllByUserId(userId);
        return skillsOfUsers.stream().map(skillMapper::toDTO).toList();
    }

    public SkillDto create(SkillDto skillDto) {
        Skill skill = skillMapper.toEntity(skillDto);
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("The skill already exists");
        }
        return skillMapper.toDTO(skillRepository.save(skill));
    }
}
