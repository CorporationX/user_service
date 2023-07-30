package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final  RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public RecommendationRequestDto getRequest(long userId) {
        RecommendationRequest foundPerson = recommendationRequestRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalStateException(String.format("There is no person in the row with id %d", userId));
        });
        return recommendationRequestMapper.toDto(foundPerson);
    }

    public List<RecommendationRequestDto> getFilterRequest(RecommendationRequestFilterDto recommendationRequestFilterDto) {
        Stream<RecommendationRequest> recommendationRequests = StreamSupport.stream(recommendationRequestRepository.findAll().spliterator(), false);
        //Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();
        for (RecommendationRequestFilter filter : recommendationRequestFilters) {
            if (filter.isApplicable(recommendationRequestFilterDto)) {
                recommendationRequests = filter.apply(recommendationRequests, recommendationRequestFilterDto);
            }
        }
        return recommendationRequestMapper.toDtoList(recommendationRequests.toList());
    }
}