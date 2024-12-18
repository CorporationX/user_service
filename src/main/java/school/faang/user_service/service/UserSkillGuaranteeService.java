package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.event.SkillAcquireEvent;
import school.faang.user_service.publisher.SkillAcquireEventPublisher;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationValidator recommendationValidator;
    private final SkillValidator skillValidator;
    private final UserService userService;
    private final SkillAcquireEventPublisher eventPublisher;

    public void addSkillGuarantee(Skill skill, Recommendation recommendation) {
        recommendationValidator.validateRecommendationExistsById(recommendation.getId());
        skillValidator.validateSkillExists(skill.getId());

        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .user(userService.findUserById(recommendation.getReceiver().getId()))
                .skill(skill)
                .guarantor(userService.findUserById(recommendation.getAuthor().getId()))
                .build();
        skill.getGuarantees().add(userSkillGuarantee);
        userSkillGuaranteeRepository.save(userSkillGuarantee);
    }

    @Transactional
    public void publishSkillAcquiredEvent(long userId, long skillId) {
        List<UserSkillGuarantee> guarantees = userSkillGuaranteeRepository
                .findByUserIdAndSkillId(userId, skillId);

        guarantees.forEach(guarantee -> {
            SkillAcquireEvent event = SkillAcquireEvent.builder()
                    .authorId(userId)
                    .receiverId(guarantee.getGuarantor().getId())
                    .skillId(skillId)
                    .build();

            eventPublisher.publish(event);
        });
    }
}
