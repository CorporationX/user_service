package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.skill.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Component
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.userRepository = userRepository;
    }

    public SkillDto create(SkillDto skill) {
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill savedSkill = skillRepository.save(skillMapper.toEntity(skill));
            return skillMapper.toDto(savedSkill);
        } else throw new DataValidationException("Навык с таким именем уже существует");
    }

    public List<SkillDto> getUserSkills(long userId) {
        if (userRepository.findById(userId).isPresent()) {
            List<Skill> skillsByUserId = skillRepository.findAllByUserId(userId);
                return skillsByUserId.stream()
                        .map(skillMapper::toDto)
                        .toList();
        } else throw new DataValidationException("Пользователя с таким id не существует");
    }
}