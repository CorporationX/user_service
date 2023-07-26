package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationMapper;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
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
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;

    public Long create(RecommendationDto recommendationDto) {

        emptyValidation(recommendationDto);
        timeValidation(recommendationDto);

        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffers();
        skillOfferDtoList.forEach(skillOfferDto -> {
            skillRepository.findById(skillOfferDto.getSkillId())
                    .ifPresent(skill -> skillOfferRepository.create(skillOfferDto.getSkillId(), skillOfferDto.getRecommendationId()));
        });

        List<Skill> skillsByReceiverId = skillRepository.findAllByUserId(recommendationDto.getReceiverId());
        for (Skill skill : skillsByReceiverId) {
            for (SkillOfferDto skillOfferDto : recommendationDto.getSkillOffers()) {
                if (skillOfferDto.getSkillId() == skill.getId()) {
                    addUserSkillGuarantee(recommendationDto.getAuthorId(),
                            recommendationDto.getReceiverId(),
                            skill.getId());
                }
            }

        }

        return recommendationRepository.create(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
    }

    public void addUserSkillGuarantee(Long authorId, Long receiverId, Long skillId) {
        User author = userRepository.findById(authorId).orElseThrow(() -> new DataValidationException("Author not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new DataValidationException("Receiver not found"));
        Skill skill = skillRepository.findById(skillId).orElseThrow(() -> new DataValidationException("Skill not found"));
        userSkillGuaranteeRepository.save(new UserSkillGuarantee(null, receiver, skill, author));
    }

    public void emptyValidation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isEmpty()) {
            throw new DataValidationException("recommendation cannot be empty");
        }
    }

    public void timeValidation(RecommendationDto recommendationDto) {
        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId()).ifPresent(lastRecommendation -> {
            if (lastRecommendation.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6))) {
                throw new DataValidationException("the recommendation can be given only after 6 months!");
            }
        });
    }
}
