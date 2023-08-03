package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final String REQUESTER_OR_RECEIVER_NOT_FOUND = "Requester or receiver not found";
    private final String REQUESTER_AND_RECEIVER_SAME = "Requester and receiver are the same";
    private final String REQUEST_IS_PENDING = "Request is pending";
    private final String SKILL_NOT_FOUND = "Skill not found";
    private final int REQUEST_TIME_LIMIT = 6;

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

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

    public RecommendationRequestDto getRequest(long id) {
        Optional<RecommendationRequest> request = repository.findById(id);
        return mapper.toDto(request.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND)));
}
