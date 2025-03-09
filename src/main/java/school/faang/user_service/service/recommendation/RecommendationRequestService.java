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
import school.faang.user_service.exception.NotFoundRequestException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterProcessor;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
                .toList();
        return requests
                .stream()
                .filter(request -> filterByCondition(request, filterDto))
                .map(recommendationRequestMapper::toRecommendationRequestDto)
                .toList();
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
        recommendationRequestRepository.save(request);
        request.setRejectionReason(rejection.getReason());
        return recommendationRequestMapper.toRecommendationRequestDto(request);
    }

    private boolean canRequestRecommendation(User requester, User receiver) {
        Optional<RecommendationRequest> request =
                recommendationRequestRepository.findLatestPendingRequest(requester.getId(), receiver.getId());
        if (request.isPresent()) {
            LocalDate lastRequestDate = request.get().getCreatedAt().toLocalDate();
            LocalDate currentDate = LocalDate.now();
            long months = ChronoUnit.MONTHS.between(lastRequestDate, currentDate);
            return months >= RECOMMENDATION_REQUEST_INTERVAL_MONTHS;
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
        RecommendationRequestFilterProcessor processor = new RecommendationRequestFilterProcessor();
        return processor.filter(Stream.of(request), filter).findAny().isPresent();
    }
}
