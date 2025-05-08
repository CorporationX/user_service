package school.faang.user_service.service.recommendationrequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.NotFoundRequestException;
import school.faang.user_service.filter.recommendationrequest.RecommendationRequestFilterProcessor;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int RECOMMENDATION_REQUEST_INTERVAL_MONTHS = 6;
    private static final String NOT_FOUND_REQUEST_MESSAGE = "Запрос не найден c id: ";
    private static final String NULL_MESSAGE = "Сообщение не может быть пустым";
    private static final String NULL_REJECT_REASON = "Причина отклонения не может быть null";
    private static final String REQUEST_ALREADY_PROCESSED = "Запрос уже обработан";
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException(NULL_MESSAGE);
        }

        Optional<User> requester = userRepository.findById(recommendationRequestDto.getRequesterId());
        Optional<User> receiver = userRepository.findById(recommendationRequestDto.getReceiverId());

        if (
                requester.isPresent() && receiver.isPresent()
                && canRequestRecommendation(requester.get(), receiver.get())
                && allSkillsExist(recommendationRequestDto.getSkills())
        ) {
            RecommendationRequest requestToCreate =
                    recommendationRequestMapper.toRecommendationRequest(recommendationRequestDto);
            recommendationRequestRepository.save(requestToCreate);
            requestToCreate
                    .getSkills()
                    .forEach(skill ->
                            skillRequestRepository.create(requestToCreate.getId(), skill.getId()));
            return recommendationRequestMapper.toRecommendationRequestDto(requestToCreate);
        }

        return null;
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        List<RecommendationRequest> requests = StreamSupport
                .stream(recommendationRequestRepository.findAll().spliterator(), false)
                .filter(request -> filterByCondition(request, filterDto))
                .toList();
        return recommendationRequestMapper.toRecommendationRequestDtoList(requests);
    }

    public RecommendationRequestDto getRequest(long requestId) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(requestId);

        if (request.isPresent()) {
            return recommendationRequestMapper.toRecommendationRequestDto(request.get());
        } else {
            throw new NotFoundRequestException(String.format(NOT_FOUND_REQUEST_MESSAGE, requestId));
        }
    }

    public RecommendationRequestDto rejectRequest(long requestId, RejectionDto rejection) {
        if (rejection.getReason() == null) {
            log.info(NULL_REJECT_REASON);
        }

        if (rejection.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException(NULL_MESSAGE);
        }

        RecommendationRequest request = recommendationRequestRepository
                .findById(requestId)
                .orElseThrow(
                        () -> new NotFoundRequestException(String.format(NOT_FOUND_REQUEST_MESSAGE, requestId))
                );

        if (request.getStatus().equals(RequestStatus.ACCEPTED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            log.warn(REQUEST_ALREADY_PROCESSED);
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        recommendationRequestRepository.save(request);
        return recommendationRequestMapper.toRecommendationRequestDto(request);
    }

    public boolean canRequestRecommendation(User requester, User receiver) {
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

    public boolean allSkillsExist(List<SkillRequestDto> skills) {
        return skills
                .stream()
                .allMatch(skill -> skillRepository.existsById(skill.getId()));
    }

    public boolean filterByCondition(RecommendationRequest request, RequestFilterDto filter) {
        RecommendationRequestFilterProcessor processor = new RecommendationRequestFilterProcessor();
        return processor.filter(Stream.of(request), filter).findAny().isPresent();
    }
}
