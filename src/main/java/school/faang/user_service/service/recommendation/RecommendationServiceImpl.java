package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.event.RecommendationRequestedEvent;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.RecommendationFilter;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;
    private final Integer repeatRecommendationTimeLimit;
    private final List<RecommendationFilter> recommendationFilters;
    private final RecommendationRequestedEventPublisher eventPublisher;

    @Autowired
    public RecommendationServiceImpl(RecommendationRepository recommendationRepository,
                                     RecommendationMapper recommendationMapper,
                                     UserContext userContext,
                                     @Value("${recommendation.repeat.limit}")
                                     Integer repeatRecommendationTimeLimit,
                                     List<RecommendationFilter> recommendationFilters,
                                     RecommendationRequestedEventPublisher eventPublisher
    ) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationMapper = recommendationMapper;
        this.userContext = userContext;
        this.repeatRecommendationTimeLimit = repeatRecommendationTimeLimit;
        this.recommendationFilters = recommendationFilters;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @Override
    public RecommendationDto create(CreateRecommendationDto newRecommendationDto) {
        authorMatchingReceiver(newRecommendationDto.receiverId(),
                "Self recommending is forbidden, but nice try...");
        latestRecommendationCheck(newRecommendationDto);

        long authorId = userContext.getUserId();
        long receiverId = newRecommendationDto.receiverId();
        String content = newRecommendationDto.content();

        long newRecommendationId = recommendationRepository.create(authorId, receiverId, content);

        eventPublisher.publish(new RecommendationRequestedEvent(authorId, receiverId, newRecommendationId));

        return recommendationMapper.toRecommendationDto(
                recommendationRepository.findById(newRecommendationId)
                        .orElseThrow(() -> new EntityNotFoundException("Newly created recommendation not found"))
        );
    }

    @Transactional
    @Override
    public RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        Recommendation recommendationToBeUpdated = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation to be updated was not found"));
        authorNotMatchingReceiver(recommendationToBeUpdated.getAuthor().getId(),
                "Can't update recommendations authored by other users");
        recommendationRepository.save(recommendationToBeUpdated);
        return recommendationMapper.toRecommendationDto(recommendationToBeUpdated);
    }

    @Transactional
    @Override
    public void delete(long recommendationId) {
        Recommendation recommendationToBeDeleted = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation to be deleted was not found"));
        authorNotMatchingReceiver(recommendationToBeDeleted.getAuthor().getId(),
                "Can't delete recommendations authored by other users");
        recommendationRepository.deleteByIdAndAuthor_id(recommendationId,
                recommendationToBeDeleted.getAuthor().getId());
    }

    @Override
    public List<RecommendationDto> getByFilters(RecommendationFilterDto passedFilters) {
        Stream<Recommendation> recommendationStream =
                recommendationRepository.findAll().stream();

        for (RecommendationFilter filter : recommendationFilters) {
            if (filter.isApplicable(passedFilters)) {
                recommendationStream = filter.apply(recommendationStream,
                        passedFilters);
            }
        }

        return recommendationStream
                .map(recommendationMapper::toRecommendationDto)
                .toList();
    }

    private void latestRecommendationCheck(CreateRecommendationDto newRecommendationDto) {
        long author = userContext.getUserId();
        long receiver = newRecommendationDto.receiverId();
        Recommendation latestRecommendation;
        try {
            latestRecommendation = recommendationRepository.findAll().stream()
                    .filter(s -> s.getAuthor().getId().equals(author)
                            && s.getReceiver().getId().equals(receiver))
                    .sorted(Comparator.comparing(Recommendation::getCreatedAt).reversed())
                    .findFirst().orElseThrow(() ->
                            new EntityNotFoundException("No recommendations are present in the DB"));
        } catch (EntityNotFoundException e) {
            return;
        }

        if (ChronoUnit.MONTHS.between(latestRecommendation.getCreatedAt(),
                LocalDateTime.now()) < repeatRecommendationTimeLimit) {
            throw new ForbiddenException("Latest recommendation was created less than "
                    + repeatRecommendationTimeLimit + " month ago");
        }
    }

    private void authorMatchingReceiver(long userId, String exceptionMessage) {
        if (userId == userContext.getUserId()) {
            throw new ForbiddenException(exceptionMessage);
        }
    }

    private void authorNotMatchingReceiver(long userId, String exceptionMessage) {
        if (userId != userContext.getUserId()) {
            throw new ForbiddenException(exceptionMessage);
        }
    }
}
