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
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto getRequest(long id){
        RecommendationRequest foundPerson = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> {
                    throw new IllegalStateException("There is no person with this id");
        });
        return recommendationRequestMapper.toDto(foundPerson);
    }
}
