package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private static final int MIN_SKILL_OFFERS = 3;

    @Autowired
    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper,
                        SkillOfferRepository skillOfferRepository) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.skillOfferRepository = skillOfferRepository;
    }

    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("That skill is already there.");
        }
        Skill entity = skillMapper.toEntity(skill);
        entity = skillRepository.save(entity);
        return skillMapper.toDto(entity);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
                    skillCandidateDto.setSkill(skillMapper.toDto(entry.getKey()));
                    skillCandidateDto.setOffersAmount(entry.getValue());
                    return skillCandidateDto;
                })
                .collect(Collectors.toList());
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User already has this skill.");
        }

        List<?> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Not enough offers to acquire this skill");
        }
        skillRepository.assignSkillToUser(skillId, userId);
        return skillMapper.toDto(skillRepository.findById(skillId).orElseThrow(() ->
                new DataValidationException("Skill not found.")));
    }
}
