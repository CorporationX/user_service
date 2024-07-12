package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.ListOfSkillsCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final ListOfSkillsCandidateMapper listOfSkillsCandidateMapper;
    private final SkillValidator skillValidator;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final long MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skill) {
        skillValidator.validateSkill(skill);
        Skill skillEntity = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream().
                map(skill -> skillMapper.toDto(skill)).collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        skillValidator.validateUserSkills(skills);
        return listOfSkillsCandidateMapper.listToSkillCandidateDto(skills);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> skill = skillRepository.findUserSkill(skillId, userId);
        if (skill.isPresent()) {
            throw new DataValidationException("the skill is already learned");
        } else {
            List<SkillOffer> offersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            if (offersOfSkill.size() >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
                addGuarantee(offersOfSkill);
                return getSkillById(skillId);
            } else throw new DataValidationException("not enough offers to acquire the skill...");
        }
    }

    public void addGuarantee(List<SkillOffer> skillOffers) {
        if (skillOffers.isEmpty()) throw new IllegalArgumentException("the list of offers is empty");
        for (SkillOffer skillOffer : skillOffers) {
            User receiver = skillOffer.getRecommendation().getReceiver();
            User author = skillOffer.getRecommendation().getAuthor();
            userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                            .user(receiver).guarantor(author).skill(skillOffer.getSkill()).build());
        }
    }

    public SkillDto getSkillById(long skillId) {
        Optional<Skill> skill = skillRepository.findById(skillId);
        if (skill.isEmpty()) throw new IllegalArgumentException("could not find any skill by this id");
        else return skillMapper.toDto(skill.get());
    }

}
