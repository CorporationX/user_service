package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    public void create(RecommendationDto recommendationDto) {
        validate(recommendationDto);
        save(recommendationDto);
        recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
    }
    public void save(RecommendationDto recommendationDto) {
        if (recommendationDto.getSkillOffers() != null && !recommendationDto.getSkillOffers().isEmpty()) {
            List<Skill> userSkills = skillRepository.findAllByUserId(recommendationDto.getReceiverId());
            List<Skill> recommendationSkills = recommendationDto.getSkillOffers().stream()
                    .map(skill -> skillRepository.findById(skill.getSkillId()).orElseThrow(() -> new DataValidationException("One of offered skill not exist in our database")))
                    .toList();
            checkAndAddSkillsGuarantor(recommendationDto, userSkills, recommendationSkills);
            recommendationDto.getSkillOffers()
                    .forEach(skill -> skillOfferRepository.create(skill.getSkillId(), skill.getRecommendationId()));
        }
    }

    private void validate(RecommendationDto recommendationDto) {
        Optional<Recommendation> recommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        if (recommendation.isPresent()) {
            LocalDateTime recommendationCreatedAt = recommendation.get().getCreatedAt();
            boolean allSkillsExistInSystem = recommendationDto.getSkillOffers().stream()
                    .allMatch(skill -> skillRepository.existsById(skill.getId()));
            if (recommendationCreatedAt.isAfter(LocalDateTime.now().minusMonths(6))) {
                throw new DataValidationException("Recommendation must be given less then 6 months");
            } else if (!allSkillsExistInSystem) {
                throw new DataValidationException("Some of the skills do not exist in our system");
            }
        }
    }

    private void checkAndAddSkillsGuarantor(RecommendationDto recommendationDto, List<Skill> userSkills, List<Skill> offeredSkills) {
        if (recommendationDto != null && userSkills != null && !userSkills.isEmpty()) {
            for (Skill userSkill : userSkills) {
                for (Skill recSkill : offeredSkills) {
                    if (userSkill.getTitle().equals(recSkill.getTitle())) {
                        var authorId = recommendationDto.getAuthorId();
                        boolean isAuthorGuarantor = userSkill.getGuarantees().stream()
                                .map(UserSkillGuarantee::getGuarantor)
                                .map(User::getId)
                                .noneMatch(guaranteeId -> guaranteeId.equals(authorId));

                        if (isAuthorGuarantor) {
                            User currentUser = userRepository
                                    .findById(recommendationDto.getReceiverId())
                                    .orElseThrow(() -> new DataValidationException("Entity not found"));
                            User guarantor = userRepository
                                    .findById(recommendationDto.getAuthorId())
                                    .orElseThrow(() -> new DataValidationException("Entity not found"));
                            UserSkillGuarantee newUserSkillGuarantee = UserSkillGuarantee
                                    .builder()
                                    .user(currentUser)
                                    .skill(userSkill)
                                    .guarantor(guarantor)
                                    .build();
                            userSkill.getGuarantees()
                                    .add(newUserSkillGuarantee);
                        } else {
                            recommendationDto
                                    .getSkillOffers()
                                    .remove(userSkill.getId());
                        }
                    }
                }
            }
            User currentUser = userRepository.findById(recommendationDto.getReceiverId()).orElseThrow(DataValidationException::new);
            currentUser.setSkills(userSkills);
            userRepository.save(currentUser);
        }
    }
}
