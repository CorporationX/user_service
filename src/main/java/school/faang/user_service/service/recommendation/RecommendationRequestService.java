package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.IllegalRequestException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterProcessor;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int RECOMMENDATION_REQUEST_INTERVAL_MONTHS = 6;
    private static final String NOT_FOUND_REQUEST_MESSAGE = "Запрос не найден c id: %d";
    private static final String NULL_MESSAGE = "Сообщение не может быть пустым";
    private static final String NULL_REJECT_REASON = "Причина отклонения не может быть null";
    private static final String REQUEST_ALREADY_PROCESSED = "Запрос уже обработан";
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestFilterProcessor recommendationRequestFilterProcessor;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        if (!validateRecommendationRequest(recommendationRequestDto)) {
            return null;
        }

        RecommendationRequest requestToCreate =
                recommendationRequestMapper.toRecommendationRequest(recommendationRequestDto);
        requestToCreate
                .getSkills()
                .forEach(skill ->
                        skillRequestRepository.create(requestToCreate.getId(), skill.getId()));
        recommendationRequestRepository.save(requestToCreate);
        return recommendationRequestMapper.toRecommendationRequestDto(requestToCreate);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        List<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream()
                .filter(request -> filterByCondition(request, filterDto))
                .toList();
        return recommendationRequestMapper.toRecommendationRequestDtoList(requests);
    }

    public RecommendationRequestDto getRequest(long requestId) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(requestId);

        if (request.isPresent()) {
            return recommendationRequestMapper.toRecommendationRequestDto(request.get());
        } else {
            throw new IllegalRequestException(String.format(NOT_FOUND_REQUEST_MESSAGE, requestId));
        }
    }

    public RecommendationRequestDto rejectRequest(long requestId, RejectionDto rejection) {
        if (rejection.getReason() == null) {
            log.error(NULL_REJECT_REASON);
            throw new IllegalArgumentException(NULL_REJECT_REASON);
        }

        if (rejection.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException(NULL_MESSAGE);
        }

        RecommendationRequest request = recommendationRequestRepository
                .findById(requestId)
                .orElseThrow(
                        () -> new IllegalRequestException(String.format(NOT_FOUND_REQUEST_MESSAGE, requestId))
                );

        if (request.getStatus().equals(RequestStatus.ACCEPTED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            log.error(REQUEST_ALREADY_PROCESSED);
            throw new IllegalArgumentException(REQUEST_ALREADY_PROCESSED);
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        recommendationRequestRepository.save(request);
        return recommendationRequestMapper.toRecommendationRequestDto(request);
    }

    private boolean canRequestRecommendation(User requester, User receiver) {
        Optional<RecommendationRequest> request =
                recommendationRequestRepository.findLatestPendingRequest(requester.getId(), receiver.getId());
        if (request.isPresent()) {
            LocalDateTime lastRequestDateTime = request.get().getCreatedAt();
            LocalDateTime nextAllowedRequestDateTime = lastRequestDateTime.plusMonths(RECOMMENDATION_REQUEST_INTERVAL_MONTHS);
            return !LocalDateTime.now().isBefore(nextAllowedRequestDateTime);
        } else {
            return true;
        }
    }

    private boolean allSkillsExist(List<SkillRequestDto> skills) {
        return skills
                .stream()
                .allMatch(skill -> skillRepository.existsById(skill.getId()));
    }

    private boolean filterByCondition(RecommendationRequest request, RequestFilterDto filter) {
        return recommendationRequestFilterProcessor.filter(Stream.of(request), filter).findAny().isPresent();
    }

    private boolean validateRecommendationRequest(RecommendationRequestDto dto) {
        if (dto.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException(NULL_MESSAGE);
        }

        Optional<User> requester = userRepository.findById(dto.getRequesterId());
        Optional<User> receiver = userRepository.findById(dto.getReceiverId());

        return requester.isPresent() && receiver.isPresent()
                && canRequestRecommendation(requester.get(), receiver.get())
                && allSkillsExist(dto.getSkills());
    }
}
