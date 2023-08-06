package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.filter.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.reccomendation.filter.RecommendationRequestFilter;

import java.time.DateTimeException;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final String REQUESTER_OR_RECEIVER_NOT_FOUND = "Requester or receiver not found";
    private final String REQUESTER_AND_RECEIVER_SAME = "Requester and receiver are the same";
    private final String REQUEST_IS_PENDING = "Request is pending";
    private final String SKILL_NOT_FOUND = "Skill not found";
    private final String REQUEST_NOT_FOUND = "Request not found";
    public static final String REQUEST_ALREADY_IS = "Recommendation request already %s";
    private final int REQUEST_TIME_LIMIT = 6;

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        checkRequestAvailability(recommendationRequest);

        long requesterId = recommendationRequest.getRequesterId();
        long receiverId = recommendationRequest.getReceiverId();
        String message = recommendationRequest.getMessage();
        recommendationRequest.getSkillRequests()
                .forEach(skillRequestDto -> skillRequestRepository.create(skillRequestDto.getId(), skillRequestDto.getSkillId()));
        return recommendationRequestMapper
                .toDto(recommendationRequestRepository.create(requesterId, receiverId, message));
    }

    public RecommendationRequestDto getRequest(long id) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(id);
        return recommendationRequestMapper.toDto(request.orElseThrow(() -> new IllegalArgumentException(REQUEST_NOT_FOUND)));
    }

    public List<RecommendationRequestDto> getRequest(RequestFilterDto filters) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();
        recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(recommendationRequests, filters));
        return recommendationRequests
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(REQUEST_NOT_FOUND));
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException(String.format(REQUEST_ALREADY_IS, request.getStatus()));
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        return recommendationRequestMapper.toDto(
                recommendationRequestRepository.save(request)
        );
    }

    private void checkRequestAvailability(RecommendationRequestDto recommendationRequest) {
        long receiverId = recommendationRequest.getReceiverId();
        long requesterId = recommendationRequest.getRequesterId();
        if (requesterId == receiverId) {
            throw new IllegalArgumentException(REQUESTER_AND_RECEIVER_SAME);
        }
        if (!isRequesterAndReceiverExist(requesterId, receiverId)) {
            throw new IllegalArgumentException(REQUESTER_OR_RECEIVER_NOT_FOUND);
        }

        Optional<RecommendationRequest> lastRequest = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);
        if (lastRequest.isPresent()) {
            LocalDateTime prevRequestsDate = lastRequest.get().getUpdatedAt();
            LocalDateTime curRequestDate = recommendationRequest.getCreatedAt();
            if (prevRequestsDate.plusMonths(REQUEST_TIME_LIMIT).isAfter(curRequestDate)) {
                throw new DateTimeException(REQUEST_IS_PENDING);
            }
        }

        checkSkillsExist(recommendationRequest);
    }

    private boolean isRequesterAndReceiverExist(long requesterId, long receiverId) {
        Optional<User> requester = userRepository.findById(requesterId);
        Optional<User> receiver = userRepository.findById(receiverId);
        return requester.isPresent() && receiver.isPresent();
    }

    private void checkSkillsExist(RecommendationRequestDto recommendationRequest) {
        recommendationRequest.getSkillRequests()
                .forEach(skillRequestDto -> {
                    if (!skillRequestRepository.existsById(skillRequestDto.getSkillId())) {
                        throw new IllegalArgumentException(SKILL_NOT_FOUND);
                    }
                });
    }
}
