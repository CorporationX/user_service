package school.faang.user_service.service.recommendation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.RecommendationFilter;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;
    private final UserRepository userRepository;
    private final List<RecommendationFilter> recommendationFilters;
    private final RecommendationValidator recommendationValidator;


    @Override
    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        recommendationValidator.validateCreate(recommendationDto);

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
    public RecommendationDto update(long recommendationId, UpdateRecommendationDto updateRecommendationDto) {
        Recommendation recommendation = getRecommendationOrFail(recommendationId);
        recommendationValidator.validateUpdate(recommendation);

        recommendationMapper.update(updateRecommendationDto, recommendation);
        recommendation = recommendationRepository.save(recommendation);
        log.info("Recommendation {} updated", recommendation.getId());
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    @Transactional
    public void delete(long recommendationId) {
        Recommendation recommendation = getRecommendationOrFail(recommendationId);
        recommendationValidator.validateDelete(recommendation);
        int deletedId = recommendationRepository.deleteByIdAndAuthor_id(
                recommendation.getId(),
                recommendation.getAuthor().getId()
        );
        log.info("Recommendation {} deleted", deletedId);
    }

    @Override
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filtersDto) {
        return applyFilters(recommendationRepository.findAll().stream(), filtersDto)
                .map(recommendationMapper::toRecommendationDto)
                .toList();
    }

    private Recommendation getRecommendationOrFail(long recommendationId) {
        return recommendationRepository
                .findById(recommendationId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Recommendation with id: " + recommendationId + "not found.")
                );
    }

    private Stream<Recommendation> applyFilters(
            Stream<Recommendation> recommendations,
            RecommendationFilterDto filtersDto
    ) {
        for (RecommendationFilter filter : recommendationFilters) {
            if (filter.isApplicable(filtersDto)) {
                recommendations = filter.apply(recommendations, filtersDto);
            }
        }
        return recommendations;
    }

}
