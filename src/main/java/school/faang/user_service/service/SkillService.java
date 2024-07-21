package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;
    private static final Logger log = LoggerFactory.getLogger(SkillService.class);
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;
    private final SkillOfferRepository offerRepository;
    private final SkillValidator skillValidator;
    private final UserSkillGuaranteeRepository guaranteeRepository;

    public SkillDto create(SkillDto skillDto) {
        skillValidator.validateSkillTitleIsNotNullAndNotBlank(skillDto);
        skillValidator.validateSkillTitleDosNotExists(skillDto);
        Skill skill = skillMapper.toEntity(skillDto);
        List<User> users = userRepository.findAllById(skillDto.getUserIds())
                .stream()
                .toList();
        skill.setUsers(users);
        return skillMapper.toDto(skillRepository.save(skill));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillMapper.toDtoSkillList(skillRepository.findAllByUserId(userId));
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        skillValidator.validateSkillExistenceById(userId);
        return skillMapper.toCandidateDtoList(skillRepository.findSkillsOfferedToUser(userId));
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);
        List<SkillOffer> allOffersOfSkill = offerRepository.findAllOffersOfSkill(skillId, userId);
        checkForEmptinessAndNumberOfGrants(userSkill, allOffersOfSkill, skillId, userId);
        return skillMapper.toDto(allOffersOfSkill.get(0).getSkill());
    }

    private void checkForEmptinessAndNumberOfGrants(Optional<Skill> userSkill,
                                                    List<SkillOffer> allOffersOfSkill,
                                                    long skillId,
                                                    long userId) {
        if (userSkill.isPresent() && allOffersOfSkill.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            addGuarantor(allOffersOfSkill);
        } else {
            log.info("the skill exists or there are no offers for the skill less than 3");
            throw new DataValidationException("The skill exists or there are no offers for the skill less than 3");
        }
    }

    private void addGuarantor(List<SkillOffer> skillOfferList) {
        guaranteeRepository.saveAll(skillOfferList.stream()
                .map(skillOffer -> UserSkillGuarantee.builder()
                        .user(skillOffer.getRecommendation().getReceiver())
                        .skill(skillOffer.skill)
                        .guarantor(skillOffer.getRecommendation().getAuthor())
                        .build())
                .distinct()
                .toList());
    }
}
