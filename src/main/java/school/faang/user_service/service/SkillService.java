package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.exception.EntityAlreadyExistException;
import school.faang.user_service.exception.notFoundExceptions.SkillNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final long MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle().toLowerCase().trim())) {
            throw new EntityAlreadyExistException("This skill already exist");
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

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillRepository.findById(skillId).orElseThrow(() -> new SkillNotFoundException("This skill doesn't exist"));
        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);

        if (userSkill.isEmpty()) {
            List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            if (allOffersOfSkill.size() >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
                addGuarantees(allOffersOfSkill);
                return skillMapper.toDTO(skill);
            }
        }
        return skillMapper.toDTO(skill);
    }

    private void addGuarantees(List<SkillOffer> allOffersOfSkill) {
        for (SkillOffer skillOffer : allOffersOfSkill) {
            User receiver = skillOffer.getRecommendation().getReceiver();
            User author = skillOffer.getRecommendation().getAuthor();
            userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                    .user(receiver)
                    .guarantor(author)
                    .skill(skillOffer.getSkill())
                    .build());
        }
    }
}
