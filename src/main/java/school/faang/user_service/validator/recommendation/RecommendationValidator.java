package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private static final int COUNT_MONTHS = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void checkNotRecommendBeforeSixMonths(long authorId, long receiverId) {
        Recommendation recommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        authorId,
                        receiverId)
                .orElseThrow(() -> {
                    log.error(ExceptionMessages.ARGUMENT_NOT_FOUND);
                    return new NoSuchElementException(ExceptionMessages.ARGUMENT_NOT_FOUND);
                });
        LocalDateTime localDateTime = LocalDateTime.now().minus(COUNT_MONTHS, ChronoUnit.MONTHS);
        if (recommendation.getUpdatedAt().isAfter(localDateTime)) {
            log.error(ExceptionMessages.RECOMMENDATION_FREQUENCY);
            throw new IllegalStateException(ExceptionMessages.RECOMMENDATION_FREQUENCY);
        }
    }

    public void validateSkillOffers(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtos = recommendationDto.getSkillOffers();
        if (skillOfferDtos == null || skillOfferDtos.isEmpty()) {
            return;
        }
        List<Long> skillOfferDtoIds = skillOfferDtos.stream().map(SkillOfferDto::getSkillId).toList(); //
        List<Skill> skills = skillRepository.findAllById(skillOfferDtoIds); //

        if (skills.size() != skillOfferDtoIds.size()) {
            log.error(ExceptionMessages.SKILL_NOT_FOUND);
            throw new DataValidationException(ExceptionMessages.SKILL_NOT_FOUND);
        }
    }
}
