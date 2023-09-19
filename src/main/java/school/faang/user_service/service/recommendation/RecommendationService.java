package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.messaging.RecommendationEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final int RECOMMENDATION_INTERVAL_MONTHS = 6;

    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationEventPublisher eventPublisher;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        validate(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.save(recommendation);
        processSkillOffers(recommendation);
        eventPublisher.publish(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        validateRecommendationToUpdate(recommendationDto);
        validate(recommendationDto);

        delete(recommendationDto.getId());
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.save(recommendation);
        processSkillOffers(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public void delete(long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllUserRecommendations(long receiverId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Recommendation> receiverRecommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable);

        return receiverRecommendations.map(recommendationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Recommendation> authorRecommendations = recommendationRepository.findAllByAuthorId(authorId, pageable);

        return authorRecommendations.map(recommendationMapper::toDto);
    }

    private void processSkillOffers(Recommendation recommendation) {
        long userId = recommendation.getReceiver().getId();
        long authorId = recommendation.getAuthor().getId();
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        List<Skill> userSkills = getUserSkills(userId);

        for (SkillOffer skillOffer : skillOffers) {
            long skillId = skillOffer.getSkill().getId();

            if (userSkills.contains(skillOffer.getSkill()) && guaranteeNotExist(userId, skillId, authorId)) {
                saveUserSkillGuarantee(userId, skillId, authorId);
            } else {
                skillOfferRepository.save(skillOffer);
            }
        }
    }

    private List<Skill> getUserSkills(long userId) {
        Optional<User> user = userRepository.findById(userId);

        return user.map(User::getSkills)
                .orElse(Collections.emptyList());
    }

    private void saveUserSkillGuarantee(long userId, long skillId, long guarantorId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not found"));
        User guarantor = userRepository.findById(guarantorId)
                .orElseThrow(() -> new DataValidationException("Guarantor not found"));

        UserSkillGuarantee guarantee = new UserSkillGuarantee();
        guarantee.setUser(user);
        guarantee.setSkill(skill);
        guarantee.setGuarantor(guarantor);

        userSkillGuaranteeRepository.save(guarantee);
    }

    private boolean guaranteeNotExist(long userId, long skillId, long guarantorId) {
        return !userSkillGuaranteeRepository.existsByUserIdAndSkillIdAndGuarantorId(userId, skillId, guarantorId);
    }

    private void validate(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skills = recommendationDto.getSkillOffers();

        validateLastUpdate(recommendationDto);
        validateSkillsListNotEmptyOrNull(skills);
        validateSkillsAreInRepository(skills);
    }

    private void validateLastUpdate(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long userId = recommendationDto.getReceiverId();

        Optional<Recommendation> lastRecommendation =
                recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, userId);

        if (lastRecommendation.isPresent()) {
            LocalDateTime lastUpdate = lastRecommendation.get().getUpdatedAt();
            LocalDateTime currentDate = LocalDateTime.now();

            if (lastUpdate.plusMonths(RECOMMENDATION_INTERVAL_MONTHS).isAfter(currentDate)) {
                String errorMessage = String.format(
                        "You've already recommended the %d user in the last %d months",
                        userId, RECOMMENDATION_INTERVAL_MONTHS);

                throw new DataValidationException(errorMessage);
            }
        }
    }

    private void validateSkillsListNotEmptyOrNull(List<SkillOfferDto> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new DataValidationException("You should choose some skills");
        }
    }

    private void validateSkillsAreInRepository(List<SkillOfferDto> skills) {
        List<Long> skillIds = getUniqueSkillIds(skills);

        for (Long skillId : skillIds) {

            if (!skillRepository.existsById(skillId)) {
                throw new DataValidationException("Invalid skills");
            }
        }
    }

    private List<Long> getUniqueSkillIds(List<SkillOfferDto> skills) {
        return skills.stream()
                .map(SkillOfferDto::getSkillId)
                .distinct()
                .toList();
    }

    private void validateRecommendationToUpdate(RecommendationDto recommendationDto) {
        recommendationRepository.findById(recommendationDto.getId())
                .orElseThrow(() -> new DataValidationException("Invalid recommendation to update"));
    }
}
