package school.faang.user_service.service.recommendation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.RecommendationFilter;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    @Value("${recommendation.delay.min.months}")
    private int minDelayMonths;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;
    private final UserRepository userRepository;
    private final List<RecommendationFilter> recommendationFilters;


    @Override
    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        Optional<Recommendation> lastRecom = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        userContext.getUserId(),
                        recommendationDto.receiverId()
                );
        if (lastRecom.isPresent() && monthsSince(lastRecom.get().getCreatedAt()) < minDelayMonths) {
            throw new DataValidationException(
                    "Cannot leave another review for same user sooner than in " + minDelayMonths + " months!"
            );
        }
        if (recommendationDto.receiverId().equals(userContext.getUserId())) {
            throw new ForbiddenException("User cannot leave recommendation for himself!");
        }

        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);
        User receiver = userRepository.getByIdOrThrow(recommendationDto.receiverId());
        recommendation.setReceiver(receiver);
        User author = userRepository.getByIdOrThrow(userContext.getUserId());
        recommendation.setAuthor(author);
        recommendation = recommendationRepository.save(recommendation);
        log.info("Recommendation {} created", recommendation.getId());
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    public RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        Recommendation recommendation = getRecommendationAndConfirmAuthorshipOrFail(recommendationId);
        recommendationMapper.update(recommendationDto, recommendation);
        recommendation = recommendationRepository.save(recommendation);
        log.info("Recommendation {} updated", recommendation.getId());
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    @Transactional
    public void delete(long recommendationId) {
        Recommendation recommendation = getRecommendationAndConfirmAuthorshipOrFail(recommendationId);
        int deletedId = recommendationRepository.deleteByIdAndAuthor_id(recommendation.getId(), recommendation.getAuthor().getId());
        log.info("Recommendation {} deleted", deletedId);
    }

    @Override
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filtersDto) {
        Stream<Recommendation> filteredRecommendations = recommendationRepository.findAll().stream();

        for (RecommendationFilter filter : recommendationFilters) {
            if (filter.isApplicable(filtersDto)) {
                filteredRecommendations = filter.apply(filteredRecommendations, filtersDto);
            }
        }

        return filteredRecommendations
                .map(recommendationMapper::toRecommendationDto)
                .toList();
    }

    private Recommendation getRecommendationAndConfirmAuthorshipOrFail(long recommendationId) {
        Recommendation recommendation = recommendationRepository
                .findById(recommendationId)
                .orElseThrow();
        long requesterId = userContext.getUserId();
        if (recommendation.getAuthor().getId() != requesterId) {
            throw new ForbiddenException("User " + requesterId + " is not the author of given recommendation!");
        }
        return recommendation;
    }

    private long monthsSince(LocalDateTime date) {
        LocalDate fromDate = date.toLocalDate();
        LocalDate today = LocalDate.now();
        return ChronoUnit.MONTHS.between(fromDate.withDayOfMonth(1), today.withDayOfMonth(1));
    }
}
