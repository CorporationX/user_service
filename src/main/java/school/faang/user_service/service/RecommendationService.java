package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utils.validationUtils.RecommendationValidation;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationService {
    public static final String ID_NULL_EXCEPTION = "Id cant be null";
    public static final String ID_AUTHOR_NULL_EXCEPTION = "Id of author can't null";
    public static final String ID_RECEIVER_NULL_EXCEPTION = "Id of receiver can't null";

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validateRecommendationDto(recommendationDto);
        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);

        recommendation = createRecommendation(recommendation);

        createSkillOffer(recommendation);
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    private Recommendation createRecommendation(Recommendation recommendation) {
        Long authorId = recommendation.getAuthor().getId();
        Long receiverId = recommendation.getReceiver().getId();
        String content = recommendation.getContent();

        Long recommendationId = recommendationRepository.create(authorId, receiverId, content);
        log.info("The recommendation was successfully created");

        recommendation.setId(recommendationId);
        return recommendation;
    }

    private void createSkillOffer(Recommendation recommendation) {
        List<SkillOffer> skillOfferListOfReceiver =
                skillOfferRepository.findAllByUserId(recommendation.getReceiver().getId());
        List<SkillOffer> skillOffersOfRecommendation = recommendation.getSkillOffers();

        for (SkillOffer skillOffer : skillOffersOfRecommendation) {
            createAndSaveSkillOffer(recommendation, skillOffer, skillOfferListOfReceiver);
        }
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        validateRecommendationDto(recommendationDto);

        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);

        updateRecommendation(recommendation);
        deleteAllAndCreate(recommendation);
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    private void updateRecommendation(Recommendation recommendation) {
        Long authorId = recommendation.getAuthor().getId();
        Long receiverId = recommendation.getReceiver().getId();
        String content = recommendation.getContent();

        recommendationRepository.update(authorId, receiverId, content);
        log.info("The recommendation was successfully updated");
    }

    private void deleteAllAndCreate(Recommendation recommendation) {
        List<SkillOffer> skillOffersOfReceiver =
                skillOfferRepository.findAllByUserId(recommendation.getReceiver().getId());

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        log.info("All skill offers have been deleted from recommendation");
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            createAndSaveSkillOffer(recommendation, skillOffer, skillOffersOfReceiver);
        }
    }

    public void delete(Long id) {
        if (id == null) {
            log.info(ID_NULL_EXCEPTION);
            throw new DataValidationException(ID_NULL_EXCEPTION);
        }

        recommendationRepository.deleteById(id);
        log.info("Recommendation with id: {} has been removed", id);
    }

    public List<RecommendationDto> getAllGivenRecommendation(Long authorId) {
        if (authorId == null) {
            log.info(ID_AUTHOR_NULL_EXCEPTION);
            throw new DataValidationException(ID_AUTHOR_NULL_EXCEPTION);
        }

        Pageable pageable = Pageable.unpaged();
        List<Recommendation> recommendationList =
                recommendationRepository.findAllByAuthorId(authorId, pageable).getContent();

        return recommendationMapper.toRecommendationDtoList(recommendationList);
    }

    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        if (receiverId == null) {
            log.info(ID_RECEIVER_NULL_EXCEPTION);
            throw new DataValidationException(ID_RECEIVER_NULL_EXCEPTION);
        }

        Pageable pageable = Pageable.unpaged();
        List<Recommendation> recommendationList =
                recommendationRepository.findAllByAuthorId(receiverId, pageable).getContent();

        return recommendationMapper.toRecommendationDtoList(recommendationList);
    }

    private void createAndSaveSkillOffer(Recommendation recommendation,
                                         SkillOffer skillOffer, List<SkillOffer> skillOfferListOfReceiver) {
        skillOfferRepository.create(skillOffer.getSkill().getId(), recommendation.getId());
        log.info("The skill offer was successfully created");

        if (skillOfferListOfReceiver.contains(skillOffer)) {
            saveUserSkillGuarantee(recommendation, skillOffer);
        }
    }

    private void saveUserSkillGuarantee(Recommendation recommendation, SkillOffer skillOffer) {
        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .user(recommendation.getReceiver())
                .skill(skillOffer.getSkill())
                .guarantor(recommendation.getAuthor())
                .build();

        skillOffer.getSkill().setGuarantees(List.of(userSkillGuarantee));
        userSkillGuaranteeRepository.save(userSkillGuarantee);
        log.info("The guarantee was successfully created");
    }

    private void validateRecommendationDto(RecommendationDto recommendationDto) {
        RecommendationValidation.validateRecommendationDtoOnNull(recommendationDto);
        RecommendationValidation.validateRecommendationContent(recommendationDto.getContent());
        RecommendationValidation.validateAuthorIdAndReceiverId(recommendationDto);
        List<SkillOffer> skillOffers = skillOfferRepository.findAllSkillOffers();
        RecommendationValidation.validateSkills(recommendationDto,
                skillOfferMapper.toSkillOfferDtoList(skillOffers));
        LocalDateTime lastRecommendationDate = getLastRecommendationDate(recommendationDto);
        RecommendationValidation.validateRecommendationDate(lastRecommendationDate);
    }

    private LocalDateTime getLastRecommendationDate(RecommendationDto recommendationDto) {
        return recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId())
                .map(Recommendation::getCreatedAt)
                .orElse(null);
    }
}
