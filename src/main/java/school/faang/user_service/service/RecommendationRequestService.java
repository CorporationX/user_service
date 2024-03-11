package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final  RecommendationRequestMapper recommendationRequestMapper;
    public RecommendationRequestDto getRequest(long userId) {
        RecommendationRequest foundPerson = recommendationRequestRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalStateException(String.format("There is no person in the row with id %d", userId));
        });
        return recommendationRequestMapper.toDto(foundPerson);
    }
}