package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.SkillValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillValidator skillValidator;
    private final SkillMapper skillMapper;

    @Transactional
    public SkillDto create(SkillDto skill) {
        skillValidator.validateSkill(skill);
        Skill skillEntity = skillMapper.toEntity(skill);
        skillEntity.setUsers(userRepository.findAllById(skill.getUserIds()));
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }
}
