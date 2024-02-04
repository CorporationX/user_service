package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.SkillValidator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillMapper skillMapper;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillValidator skillValidator;

    public SkillDto create(SkillDto skill) throws DataValidationException {
        skillValidator.checkIfSkillExists(skill.getTitle());

        Skill skillEntity = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillRepository.findUserSkill(skillId, userId).orElse(null);
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skill == null) {
            skill = getSkillIfExists(skillId);
            skillValidator.validateSkillOffersSize(offers);
            skillRepository.assignSkillToUser(skillId, userId);
        }

        setSkillGuarantees(offers, skill, userId);

        return skillMapper.toDto(skill);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);

        Map<SkillDto, Long> skillsMap = skills
                .stream()
                .collect(Collectors.groupingBy(skillMapper::toDto, Collectors.counting()));

        List<SkillCandidateDto> skillCandidateDtos = skillsMap
                .entrySet().stream()
                .map(item -> new SkillCandidateDto(item.getKey(), item.getValue()))
                .toList();

        return skillCandidateDtos;
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skillMapper.listToDto(skills);
    }

    private void setSkillGuarantees(List<SkillOffer> offers, Skill skill, long userId) {
        for (SkillOffer offer : offers) {
            User receiver = offer.getRecommendation().getReceiver();
            User author = offer.getRecommendation().getAuthor();

            UserSkillGuarantee guarantor = UserSkillGuarantee.builder()
                    .id(userId)
                    .user(receiver)
                    .skill(skill)
                    .guarantor(author)
                    .build();

            userSkillGuaranteeRepository.save(guarantor);
        }
    }

    private Skill getSkillIfExists(Long skillId) {
        return skillRepository.findById(skillId).orElseThrow(
                () -> new DataValidationException("Skill doesn't exist!")
        );
    }
}
