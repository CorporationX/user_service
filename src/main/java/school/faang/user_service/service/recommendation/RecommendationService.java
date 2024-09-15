package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationServiceHandler recommendationServiceHandler;


    @Transactional
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        User author = getUserById(recommendationDto.getAuthorId());
        User receiver = getUserById(recommendationDto.getReceiverId());
        recommendationServiceHandler.selfRecommendationValidation(author, receiver);
        recommendationServiceHandler.recommendationIntervalValidation(author, receiver);

        List<Long> skillOfferDtoIds = getSkillOfferDtoIds(recommendationDto);
        List<Long> allSkillsIds = getAllSkillsIds(skillOfferDtoIds);
        recommendationServiceHandler.skillOffersValidation(skillOfferDtoIds, allSkillsIds);

        skillOfferDtoIds.forEach(skillId -> createRecommendationWithSkillOfferAndSkillGuarantee(
                author.getId(),
                receiver.getId(),
                recommendationDto.getContent(),
                skillId)
        );
        return recommendationDto;
    }

    private void createRecommendationWithSkillOfferAndSkillGuarantee(Long authorId, Long receiverId, String content, Long skillId) {
        if (recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId).isEmpty()) {
            recommendationRepository.createRecommendationWithSkillOffer(
                    authorId,
                    receiverId,
                    content,
                    skillId
            );
        }
        if (!userSkillGuaranteeRepository.existsUserSkillGuaranteeByUserIdAndSkillId(receiverId, skillId)) {
            userSkillGuaranteeRepository.create(authorId, receiverId, skillId);
        } else {
            throw new DataValidationException("Recommendation or Guarantee for this SkillOffer: " + skillId +
                    " from this Author: " + authorId + " already exist");
        }
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        User author = getUserById(recommendationDto.getAuthorId());
        User receiver = getUserById(recommendationDto.getReceiverId());
        recommendationServiceHandler.selfRecommendationValidation(author, receiver);
        recommendationServiceHandler.recommendationIntervalValidation(author, receiver);

        List<Long> skillOfferDtoIds = getSkillOfferDtoIds(recommendationDto);
        List<Long> allSkillsIds = getAllSkillsIds(skillOfferDtoIds);
        recommendationServiceHandler.skillOffersValidation(skillOfferDtoIds, allSkillsIds);

        skillOfferDtoIds.forEach(skillId -> updateRecommendationWithSkillOfferAndSkillGuarantee(
                author.getId(),
                receiver.getId(),
                recommendationDto.getContent(),
                skillId)
        );

        return recommendationDto;
    }

    private void updateRecommendationWithSkillOfferAndSkillGuarantee(Long authorId, Long receiverId, String content, Long skillId) {
        if (recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId).isEmpty()) {
            recommendationRepository.updateRecommendationWithSkillOffer(
                    authorId,
                    receiverId,
                    content,
                    skillId
            );
            if (!userSkillGuaranteeRepository.existsUserSkillGuaranteeByUserIdAndSkillId(receiverId, skillId)) {
                userSkillGuaranteeRepository.create(authorId, receiverId, skillId);
            } else {
                throw new DataValidationException("Recommendation or Guarantee for this SkillOffer: " + skillId +
                        " from this Author: " + authorId + " already exist");
            }
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new DataValidationException("User with ID: " + userId + " not found")
        );
    }

    private List<Long> getAllSkillsIds(List<Long> skillOfferDtoIds) {
        return skillRepository.findAllById(skillOfferDtoIds)
                .stream()
                .map(Skill::getId)
                .toList();
    }

    private List<Long> getSkillOfferDtoIds(RecommendationDto recommendationDto) {
        return recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
    }
}
