package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
public class SkillService {
    private SkillRepository skillRepository;
    private SkillMapper mapper;

    @Autowired
    public SkillService(SkillRepository skillRepository, SkillMapper mapper) {
        this.skillRepository = skillRepository;
        this.mapper = mapper;
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> allByUserId = skillRepository.findAllByUserId(userId);
        verifyUserExist(allByUserId);
        return mapSkillToDto(allByUserId);
    }

    private void verifyUserExist(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new DataValidationException("User with that id doesn't exist");
        }
    }

    private List<SkillDto> mapSkillToDto(List<Skill> skills) {
        return skills.stream()
                .map((skill -> mapper.toDto(skill)))
                .toList();
    }
    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("This skill already exist");
        }
        Skill savedSkill = skillRepository.save(mapper.toEntity(skill));
        return mapper.toDto(savedSkill);
    }
}
