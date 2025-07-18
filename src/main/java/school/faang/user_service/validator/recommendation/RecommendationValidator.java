package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.recommendation.AnotherAuthorException;
import school.faang.user_service.exception.recommendation.RecommendationCooldownException;
import school.faang.user_service.exception.recommendation.SelfRecommendationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    @Value("${recommendation.delay.min.months}")
    private int minDelayMonths;
    private final UserContext userContext;
    private final RecommendationRepository recommendationRepository;

    public void validateCreate(CreateRecommendationDto createRecommendationDto) {
        validateNotCreatingForSelf(createRecommendationDto);
        validateMinCooldownPeriod(createRecommendationDto);
    }

    private void validateMinCooldownPeriod(CreateRecommendationDto createRecommendationDto) {
        Optional<Recommendation> lastRecom = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        userContext.getUserId(),
                        createRecommendationDto.receiverId()
                );
        if (lastRecom.isPresent() && monthsSince(lastRecom.get().getCreatedAt()) < minDelayMonths) {
            throw new RecommendationCooldownException(
                    "Cannot leave another review for same user sooner than in " + minDelayMonths + " months!"
            );
        }
    }

    private void validateNotCreatingForSelf(CreateRecommendationDto createRecommendationDto) {
        if (createRecommendationDto.receiverId().equals(userContext.getUserId())) {
            throw new SelfRecommendationException();
        }
    }

    private long monthsSince(LocalDateTime date) {
        LocalDate fromDate = date.toLocalDate();
        LocalDate today = LocalDate.now();
        return ChronoUnit.MONTHS.between(fromDate.withDayOfMonth(1), today.withDayOfMonth(1));
    }

    public void validateUpdate(Recommendation recommendation) {
        validateAuthorship(recommendation);
    }

    public void validateDelete(Recommendation recommendation) {
        validateAuthorship(recommendation);
    }

    private void validateAuthorship(Recommendation recommendation) {
        long requesterId = userContext.getUserId();
        if (recommendation.getAuthor().getId() != requesterId) {
            throw new AnotherAuthorException("User " + requesterId + " is not the author of given recommendation!");
        }
    }

}
