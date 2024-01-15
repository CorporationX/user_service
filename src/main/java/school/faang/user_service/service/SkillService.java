package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.skill.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validate.skill.SkillValidation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final UserRepository userRepository;
    private final SkillValidation skillValidation;
    private static final long MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skill) {
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill savedSkill = skillRepository.save(skillMapper.toEntity(skill));
            return skillMapper.toDto(savedSkill);
        } else {
            throw new DataValidationException("Такой навык уже существует");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {

        skillValidation.validateUserId(userId);

        List<Skill> skillsByUserId = skillRepository.findAllByUserId(userId);
        return skillsByUserId.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {

        skillValidation.validateUserId(userId);

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

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        skillValidation.validateUserId(userId);
        skillValidation.validateSkillId(skillId);

        Skill skill = Skill.builder().id(skillId).build();
        SkillDto skillDto = skillMapper.toDto(skill);

        if (skillRepository.findUserSkill(skillId, userId).isEmpty()) {
            List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

            if (allOffersOfSkill.size() >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
                User user = User.builder().id(userId).build();
                allOffersOfSkill.forEach((offer) -> {
                    User guarantor = new User();
                    guarantor.setId(offer.getRecommendation().getAuthor().getId());
                    userSkillGuaranteeRepository.save(new UserSkillGuarantee(null, user, skill, guarantor));
                });
            }
        }
        return skillDto;
    }
}