package school.faang.user_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

@Component
public class RecommendationService {

    public static final int MIN_RECOMMEND_PERIOD = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferService skillOfferService;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    @Autowired
    public RecommendationService(RecommendationRepository recommendationRepository,
            SkillOfferService skillOfferService,
            SkillRepository skillRepository, UserRepository userRepository,
            UserSkillGuaranteeRepository userSkillGuaranteeRepository,
            RecommendationMapper recommendationMapper) {
        this.recommendationRepository = recommendationRepository;
        this.skillOfferService = skillOfferService;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.userSkillGuaranteeRepository = userSkillGuaranteeRepository;
        this.recommendationMapper = recommendationMapper;
    }

    public RecommendationDto create(RecommendationDto recommendation) {
        validateLastRecommendDate(recommendation.authorId(), recommendation.receiverId());

        if (recommendation.skillOffers() != null && !recommendation.skillOffers().isEmpty()) {
            validateSkillsExistenceInDB(recommendation.skillOffers());
            checkPresenceUserSkills(recommendation);
        }

        Long recommendId = recommendationRepository.create(recommendation.authorId(), recommendation.receiverId(),
                recommendation.content());

        if (recommendation.skillOffers() != null) {
            skillOfferService.saveSkillOffers(recommendation.skillOffers(), recommendId);
        }

        Recommendation result = recommendationRepository.findById(recommendId)
                .orElseThrow(() -> new DataValidationException("Recommendation with ID " + recommendId + " is absent in database"));

        return recommendationMapper.toDto(result);
    }

    public RecommendationDto update(RecommendationDto updatedRecommend) {
        validateLastRecommendDate(updatedRecommend.authorId(), updatedRecommend.receiverId());

        if (updatedRecommend.skillOffers() != null && !updatedRecommend.skillOffers().isEmpty()) {
            validateSkillsExistenceInDB(updatedRecommend.skillOffers());
            skillOfferService.deleteAllSkillOffers(updatedRecommend.id());
            skillOfferService.saveSkillOffers(updatedRecommend.skillOffers(), updatedRecommend.id());
        }

        checkPresenceUserSkills(updatedRecommend);
        recommendationRepository.update(updatedRecommend.authorId(), updatedRecommend.receiverId(),
                updatedRecommend.content());
        return updatedRecommend;
    }

    public void delete(long recommendId) {
        recommendationRepository.deleteById(recommendId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> allRecommendation = recommendationRepository.findAllByReceiverId(receiverId,
                Pageable.unpaged());

        return allRecommendation.stream().map(recommendationMapper::toDto).toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> allGivenRecommendations = recommendationRepository.findAllByAuthorId(authorId,
                Pageable.unpaged());

        return allGivenRecommendations.stream().map(recommendationMapper::toDto).toList();
    }

    private void checkPresenceUserSkills(RecommendationDto recommendation) {
        List<Long> skillIdsFromOffer = recommendation.skillOffers().stream().map(SkillOfferDto::skillId).toList();
        List<Skill> extendsSkillIds = skillRepository.findAllByUserId(recommendation.receiverId()).stream()
                .filter(skill -> skillIdsFromOffer.contains(skill.getId()))
                .toList();

        if (!extendsSkillIds.isEmpty()) {
            extendsSkillIds.forEach(skill -> {
                boolean isNeedToSaveGuarantor = skill.getGuarantees()
                        .stream()
                        .noneMatch(guarantee -> guarantee.getGuarantor().getId().equals(recommendation.authorId()));

                if (isNeedToSaveGuarantor) {
                    User guarantor = userRepository.findById(recommendation.authorId())
                            .orElseThrow(() -> new DataValidationException(
                                    "User with ID " +
                                            recommendation.authorId() +
                                            " is absent in database"
                            ));
                    User receiver = userRepository.findById(recommendation.receiverId())
                            .orElseThrow(() -> new DataValidationException(
                                    "User with ID " +
                                            recommendation.receiverId() +
                                            " is absent in database"
                            ));
                    userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                            .user(receiver)
                            .skill(skill)
                            .guarantor(guarantor)
                            .build());
                }
            });
        }
    }

    private void validateLastRecommendDate(Long authorId, Long receiverId) {
        LocalDateTime today = LocalDateTime.now();
        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        if (lastRecommendation.isPresent()) {
            if (!lastRecommendation.get().getCreatedAt().isAfter(today.plusMonths(-MIN_RECOMMEND_PERIOD))) {
                throw new DataValidationException("Last recommendation was earlier than 6 months");
            }
        }
    }

    private void validateSkillsExistenceInDB(List<SkillOfferDto> skillOffers) {
        List<Long> skillIds = skillOffers.stream().map(SkillOfferDto::skillId).toList();

        if (skillRepository.countExisting(skillIds) != skillIds.size()) {
            throw new DataValidationException("One or more skills are absent in database");
        }
    }
}