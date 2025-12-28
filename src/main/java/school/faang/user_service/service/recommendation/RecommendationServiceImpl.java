package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationEventDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.messages.kafka.producers.RecommendationPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final RecommendationPublisher recommendationPublisher;
    @Value("${recommendation.time.limit}")
    private int limit;

    @Override
    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        var authorId = userContext.getUserId();
        if (authorId == recommendationDto.receiverId()) {
            throw new DataValidationException("You can't send a recommendation to oneself");
        }

        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId,
                        recommendationDto.receiverId())
                .ifPresent(recommendation -> {
                    if (recommendation.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(limit))) {
                        throw new DataValidationException("You can't send a recommendation more often: "
                                + limit + " months");
                    }
                });

        User author = userRepository.getByIdOrThrow(authorId);
        User receiver = userRepository.getByIdOrThrow(recommendationDto.receiverId());

        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setContent(recommendationDto.content());

        Recommendation saved = recommendationRepository.save(recommendation);


        recommendationPublisher.publish(RecommendationEventDto.builder()
                .authorId(saved.getAuthor().getId())
                .receiverId(saved.getReceiver().getId())
                .content(saved.getContent())
                .build());

        return RecommendationDto.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .authorId(authorId)
                .receiverId(recommendationDto.receiverId())
                .build();
    }

    @Override
    public RecommendationDto update(UpdateRecommendationDto dto, Long id) {
        var userId = userContext.getUserId();
        var recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation not found"));

        if (userId != recommendation.getAuthor().getId()) {
            throw new ForbiddenException("Access denied");
        }

        recommendation.setContent(dto.content());

        return RecommendationDto.builder()
                .id(id)
                .authorId(recommendation.getAuthor().getId())
                .receiverId(recommendation.getReceiver().getId())
                .content(dto.content())
                .build();
    }

    @Override
    public void delete(Long recommendationId) {
        var userId = userContext.getUserId();
        var authorId = recommendationRepository.findAuthorIdById(recommendationId)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation not found"));

        if (userId != authorId) {
            throw new ForbiddenException("Access denied");
        }

        recommendationRepository.deleteByIdAndAuthor_id(recommendationId, authorId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<RecommendationDto> getByFilters(RecommendationFilterDto filters, Pageable pageable) {
        var page = recommendationRepository.findByFilters(
                filters.contentContains(),
                filters.receiverId(),
                filters.authorId(),
                pageable
        );

        List<RecommendationDto> content = page.getContent().stream()
                .map(recommendation -> RecommendationDto.builder()
                        .id(recommendation.getId())
                        .authorId(recommendation.getAuthor().getId())
                        .receiverId(recommendation.getReceiver().getId())
                        .content(recommendation.getContent())
                        .build())
                .toList();

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }
}
