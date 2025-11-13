package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationResponseDto;
import school.faang.user_service.dto.recommendation.FilterRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filters.recommendation.RecommendationFilter;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.config.context.UserContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Реализация RecommendationService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final List<RecommendationFilter> recommendationFilters;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;
    private final SkillOfferRepository skillOfferRepository;

    @Value("${recommendation.cooldown.months:6}")
    private int cooldownMonths;

    @Override
    public RecommendationResponseDto create(CreateRecommendationRequestDto request) {
        final Long authorId = userContext.getUserId();
        final Long receiverId = request.receiverId();
        final String content = request.content();

        log.info("Creating recommendation: authorId={}, receiverId={}", authorId, receiverId);

        if (receiverId == null) {
            throw new DataValidationException("receiverId is required");
        }
        if (isBlank(content)) {
            throw new DataValidationException("content must not be blank");
        }
        if (Objects.equals(authorId, receiverId)) {
            throw new ForbiddenException("You cannot create recommendation for yourself");
        }

        final LocalDateTime boundary = LocalDateTime.now().minusMonths(cooldownMonths);
        boolean violatesCooldown = recommendationRepository.findAll().stream()
                .filter(r -> Objects.equals(r.getAuthor().getId(), authorId))
                .filter(r -> Objects.equals(r.getReceiver().getId(), receiverId))
                .anyMatch(r -> {
                    LocalDateTime createdAt = r.getCreatedAt();
                    return createdAt != null && createdAt.isAfter(boundary);
                });

        if (violatesCooldown) {
            throw new DataValidationException(
                    "You can leave a recommendation for this user only once in " + cooldownMonths + " months");
        }

        Long createdRecommendationId = recommendationRepository.create(authorId, receiverId, content);
        log.debug("Recommendation created with id={}", createdRecommendationId);

        if (request.skillIds() != null && !request.skillIds().isEmpty()) {
            for (Long skillId : request.skillIds()) {
                if (skillId != null) {
                    skillOfferRepository.create(skillId, createdRecommendationId);
                }
            }
        }

        Recommendation created = recommendationRepository.findById(createdRecommendationId)
                .orElseThrow(() -> new DataValidationException("Created recommendation not found by id="
                        + createdRecommendationId));

        return recommendationMapper.toResponse(created);
    }

    @Override
    public RecommendationResponseDto update(long recommendationId, UpdateRecommendationRequestDto request) {
        final Long currentUserId = userContext.getUserId();
        log.info("Updating recommendation id={} by user={}", recommendationId, currentUserId);

        if (request == null || isBlank(request.content())) {
            throw new DataValidationException("content must not be blank");
        }

        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataValidationException("Recommendation not found: id=" + recommendationId));

        if (!Objects.equals(recommendation.getAuthor().getId(), currentUserId)) {
            throw new ForbiddenException("You can update only your own recommendation");
        }

        recommendation.setContent(request.content());
        Recommendation updatedRecommendation = recommendationRepository.save(recommendation);

        if (request.skillIds() != null) {
            skillOfferRepository.deleteAllByRecommendationId(recommendationId);
            for (Long skillId : request.skillIds()) {
                if (skillId != null) {
                    skillOfferRepository.create(skillId, recommendationId);
                }
            }
        }

        return recommendationMapper.toResponse(updatedRecommendation);
    }

    @Override
    public void delete(long recommendationId) {
        final long currentUserId = userContext.getUserId();
        log.info("Deleting recommendation id={} by user={}", recommendationId, currentUserId);

        int deletedRecommendation = recommendationRepository.deleteByIdAndAuthor_id(recommendationId, currentUserId);
        if (deletedRecommendation == 0) {
            throw new ForbiddenException("You can delete only your own recommendation or it does not exist");
        }

        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
        log.debug("Recommendation id={} deleted", recommendationId);
    }

    @Override
    public List<RecommendationResponseDto> getByFilters(FilterRecommendationRequestDto filters) {
        log.info("Fetching recommendations by filters: {}", filters);

        Stream<Recommendation> recommendationsStream = recommendationRepository.findAll().stream();

        // Apply each filter if applicable
        for (RecommendationFilter filter : recommendationFilters) {
            if (filter.isApplicable(filters)) {
                recommendationsStream = filter.apply(recommendationsStream, filters);
            }
        }

        return recommendationsStream
                .map(recommendationMapper::toResponse)
                .collect(Collectors.toList());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

}
