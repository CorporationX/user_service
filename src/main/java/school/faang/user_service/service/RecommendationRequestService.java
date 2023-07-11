package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository repository;
    private final RecommendationRequestMapper mapper;

    public final static String NOT_FOUND = "Recommendation request not found";

    public RecommendationRequestDto getRequest(long id) {
        Optional<RecommendationRequest> request = repository.findById(id);
        return mapper.toDto(request.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND)));
    }
}
