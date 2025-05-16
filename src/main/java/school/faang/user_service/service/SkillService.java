package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository offerRepository;
    private final UserSkillGuaranteeRepository guaranteeRepository;
    private final UserRepository userRepository;
    private static final int MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill newSkill = skillRepository.save(skillMapper.toEntity(skill));
            return skillMapper.toDto(newSkill);
        }
        throw new DataValidationException("Skill " + skill.getTitle() + " already exists!");
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .map(skillMapper::toDto)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> skillMapper.toSkillCandidateDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User already have this skill!");
        }
        if (offerRepository.findAllOffersOfSkill(skillId, userId).size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Not enough skill offers!");
        }
        skillRepository.assignSkillToUser(skillId, userId);
        for (SkillOffer offer : offerRepository.findAllOffersOfSkill(skillId, userId)) {
            UserSkillGuarantee skillGuarantor =
                    skillMapper.toUserSkillGuarantee(userRepository, offer, userId);
            guaranteeRepository.save(skillGuarantor);
        }
        return skillMapper.toDto(skillRepository.getReferenceById(skillId));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toDto)
                .toList();
    }

    private void validateSkill(SkillDto skill) {
        if (Objects.isNull(skill)) {
            log.error("The SkillDto submitted in method validateSkill is null!");
            throw new DataValidationException("SkillDto from argument is null!");
        }
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            log.error("The SkillDto submitted to method validateSkill doesn't have a name!");
            throw new DataValidationException("SkillDto has no name!");
        }
    }
}
