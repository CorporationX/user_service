package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;

    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle().toLowerCase().trim())) {
            throw new DataValidationException("This skill already exist");
        }
        Skill savedSkill = skillRepository.save(skillMapper.toEntity(skill));
        return skillMapper.toDTO(savedSkill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skill -> skillMapper.toDTO(skill))
                .collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillOfferRepository.findById(userId).stream()
                .map(skillOffer -> skillMapper.candidateToDTO(skillOffer))
                .collect(Collectors.toList());
    }
}
