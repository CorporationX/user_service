package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
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
        List<Long> skillOfferDtoIds = getSkillOfferDtoIds(recommendationDto);

        createUpdateRecommendationValidation(author, receiver, skillOfferDtoIds);

        skillOfferDtoIds.forEach(skillId -> createRecommendationWithSkillOfferAndSkillGuarantee(
                author.getId(),
                receiver.getId(),
                recommendationDto.getContent(),
                skillId)
        );
        return recommendationDto;
    }

    @Transactional
    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        User author = getUserById(recommendationDto.getAuthorId());
        User receiver = getUserById(recommendationDto.getReceiverId());
        List<Long> skillOfferDtoIds = getSkillOfferDtoIds(recommendationDto);

        createUpdateRecommendationValidation(author, receiver, skillOfferDtoIds);

        skillOfferDtoIds.forEach(skillId -> updateRecommendationWithSkillOfferAndSkillGuarantee(
                author.getId(),
                receiver.getId(),
                recommendationDto.getContent(),
                skillId)
        );
        return recommendationDto;
    }

    @Transactional
    public void deleteRecommendation(long recommendationId) {
        recommendationServiceHandler.recommendationExistsByIdValidation(recommendationId);
        recommendationRepository.deleteById(recommendationId);
    }

    @Transactional(readOnly = true)
    public List<Recommendation> getAllUserRecommendations(Long receiverId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return recommendationRepository.findAllByReceiverId(receiverId, pageable).getContent();
    }

    @Transactional(readOnly = true)
    public List<Recommendation> getAllGivenRecommendations(long authorId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return recommendationRepository.findAllByAuthorId(authorId, pageable).getContent();
    }


    private void createRecommendationWithSkillOfferAndSkillGuarantee(Long authorId, Long receiverId, String content, Long skillId) {
        if (recommendationRepository.recommendationExistCheck(receiverId, skillId) < 1) {
            recommendationRepository.createRecommendationWithSkillOffer(
                    authorId,
                    receiverId,
                    content,
                    skillId
            );
        } else {
            if (userSkillGuaranteeRepository.findFirstByGuarantorIdAndUserIdAndSkillIdOrderById(authorId, receiverId, skillId).isEmpty()) {
                userSkillGuaranteeRepository.create(authorId, receiverId, skillId);
            } else {
                throw new DataValidationException(
                        "Guarantee for SkillOffer with ID: " + skillId +
                        " for User with ID: " + receiverId +
                        " from Guarantor with ID: " + authorId +
                        " already exist."
                );
            }
        }
    }

    private void updateRecommendationWithSkillOfferAndSkillGuarantee(Long authorId, Long receiverId, String content, Long skillId) {
        if (recommendationRepository.recommendationExistCheck(receiverId, skillId) == 1) {
            recommendationRepository.updateRecommendationWithSkillOffer(
                    authorId,
                    receiverId,
                    content,
                    skillId
            );
        } else {
            if (userSkillGuaranteeRepository.findFirstByGuarantorIdAndUserIdAndSkillIdOrderById(authorId, receiverId, skillId).isEmpty()) {
                userSkillGuaranteeRepository.create(authorId, receiverId, skillId);
            } else {
                throw new DataValidationException(
                        "Guarantee for SkillOffer with ID: " + skillId +
                        " for User with ID: " + receiverId +
                        " from Guarantor with ID: " + authorId +
                        " already exist."
                );
            }
        }
    }

    private void createUpdateRecommendationValidation(User author, User receiver, List<Long> skillOfferDtoIds) {
        recommendationServiceHandler.selfRecommendationValidation(author, receiver);
        recommendationServiceHandler.recommendationIntervalValidation(author, receiver);

        List<Long> allSkillsIds = getAllSkillsIds(skillOfferDtoIds);
        recommendationServiceHandler.skillOffersValidation(skillOfferDtoIds, allSkillsIds);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new DataValidationException("User with ID: " + userId + " not found.")
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
