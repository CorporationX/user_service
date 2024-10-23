package school.faang.user_service.service.skill;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.UserSkillGuaranteeService;

import java.util.List;

@Service
public class SkillOfferService {

    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeService userSkillGuaranteeService;
    private final SkillService skillService;
    private final UserService userService;

    @Autowired
    public SkillOfferService(SkillOfferRepository skillOfferRepository,
                             UserSkillGuaranteeService userSkillGuaranteeService,
                             @Lazy SkillService skillService,
                             UserService userService) {
        this.skillOfferRepository = skillOfferRepository;
        this.userSkillGuaranteeService = userSkillGuaranteeService;
        this.skillService = skillService;
        this.userService = userService;
    }

    public List<SkillOffer> findAllOffersOfSkill(Skill skill, User user) {
        return skillOfferRepository.findAllOffersOfSkill(skill.getId(), user.getId());
    }

    public void addSkillsWithGuarantees(List<Skill> skills, Long recommendationId, RecommendationDto recommendationDto) {
        User receiver = userService.getUserById(recommendationDto.getReceiverId());
        User author = userService.getUserById(recommendationDto.getAuthorId());

        for (Skill skill : skills) {
            skillOfferRepository.create(skill.getId(), recommendationId);

            if (receiver.getSkills().contains(skill)) {
                UserSkillGuarantee existedGuarantee = userSkillGuaranteeService
                        .saveUserSkillGuarantee(skill, receiver, author);

                skill.getGuarantees().add(existedGuarantee);
                skillService.saveSkill(skill);
            }
        }
    }

    public void deleteAllByRecommendationId(Long id) {
        skillOfferRepository.deleteAllByRecommendationId(id);
    }
}
