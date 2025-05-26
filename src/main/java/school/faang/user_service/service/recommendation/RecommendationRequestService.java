package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestRejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class RecommendationRequestService {
    private static final int REQUEST_LIMIT_PER_DAYS = 180;

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;

    private final RecommendationRequestMapper recommendationRequestMapper;

    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);

        userRepository.findById(recommendationRequestDto.getRequesterId()).ifPresentOrElse(
                recommendationRequest::setRequester,
                () -> {
                    throw new DataValidationException("Requester not found");
                }
        );

        userRepository.findById(recommendationRequestDto.getReceiverId()).ifPresentOrElse(
                recommendationRequest::setReceiver,
                () -> {
                    throw new DataValidationException("Receiver not found");
                }
        );

        recommendationRequest.setStatus(RequestStatus.PENDING);

        recommendationRequestRepository.findLatestPendingRequest(
                recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()
        ).ifPresent(
                (latestPendingRequest) -> {
                    if (Duration.between(latestPendingRequest.getCreatedAt(), LocalDateTime.now()).toDays() < REQUEST_LIMIT_PER_DAYS) {
                        throw new DataValidationException("Recommendation can be requested once in " + REQUEST_LIMIT_PER_DAYS + " days");
                    }
                }
        );

        return recommendationRequestMapper.toDto(
                recommendationRequestRepository.save(recommendationRequest)
        );
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto filterDto) {
        Stream<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream();

        for (RecommendationRequestFilter filter : recommendationRequestFilters) {
            if (filter.isApplicable(filterDto)) {
                requests = filter.apply(requests, filterDto);
            }
        }

        return requests.map(recommendationRequestMapper::toDto).toList();
    }

    private RecommendationRequest findRecommendationRequest(Long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recommendation request not found")
        );
    }

    public RecommendationRequestDto getRequest(Long id) {
        return recommendationRequestMapper.toDto(
                findRecommendationRequest(id)
        );
    }

    public RecommendationRequestDto rejectRequest(Long id, RecommendationRequestRejectionDto rejection) {
        RecommendationRequest request = findRecommendationRequest(id);

        if (request.getStatus() == RequestStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request already accepted");
        }

        if (request.getStatus() == RequestStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request already rejected");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());

        return recommendationRequestMapper.toDto(
                recommendationRequestRepository.save(request)
        );
    }
}
