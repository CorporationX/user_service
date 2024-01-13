package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.skill.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Component
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final UserRepository userRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper, SkillCandidateMapper skillCandidateMapper, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.skillCandidateMapper = skillCandidateMapper;
        this.userRepository = userRepository;
    }

    public SkillDto create(SkillDto skill) {
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill savedSkill = skillRepository.save(skillMapper.toEntity(skill));
            return skillMapper.toDto(savedSkill);
        } else throw new DataValidationException("Навык с таким именем уже существует");
    }

    public List<SkillDto> getUserSkills(long userId) {

        validateUserId(userId);

        List<Skill> skillsByUserId = skillRepository.findAllByUserId(userId);
        return skillsByUserId.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {

        validateUserId(userId);

        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);

        return skillsOfferedToUser.stream()
                .distinct()
                .map(skillCandidateMapper::toDto)
                .peek((skillCandidateDto -> {
                    long countSkill = skillsOfferedToUser.stream()
                            .filter((skill) -> skillCandidateDto.getSkillDto().getId().equals(skill.getId()))
                            .count();
                    skillCandidateDto.setOffersAmount(countSkill);
                }))
                .toList();
    }

    private void validateUserId(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new DataValidationException("Пользователя с таким id не существует");
        }
    }
}