package school.faang.user_service.service.recommendationRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.dto.recommendationRequest.RejectionRequestDto;
import school.faang.user_service.dto.recommendationRequest.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.mapper.recommendationRequest.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.filter.recomendation.RequestFilter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int COUNT_MONTHS = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final List<RequestFilter> requestFilter;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        checkExistenceObjectByIds(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getRecieverId());
        heckForRequestsOfSixMonths(recommendationRequestDto);
        List<SkillRequest> skills = checkSkillRequestInDatabase(recommendationRequestDto);

        RecommendationRequest newRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);
        recommendationRequestRepository.save(newRequest);

        skills.forEach(skill -> skillRequestRepository.create(newRequest.getId(), skill.getId()));

        return recommendationRequestMapper.toDto(newRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        Stream<RecommendationRequest> recommendationRequestsAll = recommendationRequestRepository.findAll().stream();
        List<RecommendationRequest> result = requestFilter.stream()
                .filter(filterOne -> filterOne.isApplication(filter))
                .reduce(recommendationRequestsAll, (cumulativeStream, filterOne) ->
                        filterOne.apply(cumulativeStream, filter), Stream::concat)
                .toList();
        return result.stream()
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = recommendationRequestRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
                    return new NoSuchElementException(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
                });
        return recommendationRequestMapper.toDto(request);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionRequestDto rejectionRequestDto) {
        return recommendationRequestRepository.findById(id)
                .map(request -> {
                    if (request.getStatus() == RequestStatus.PENDING) {
                        request.setStatus(RequestStatus.REJECTED);
                        request.setRejectionReason(rejectionRequestDto.getReason());
                        recommendationRequestRepository.save(request);
                        return recommendationRequestMapper.toDto(request);
                    } else {
                        log.error(ExceptionMessages.DISCREPANCY_OF_STATUS);
                        throw new IllegalStateException(ExceptionMessages.DISCREPANCY_OF_STATUS);
                    }
                }).orElseThrow(() -> {
                    log.error(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
                    return new NoSuchElementException(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
                });
    }

    private void checkExistenceObjectByIds(long requesterId, long recieverId) {
        if (!userRepository.existsById(requesterId) || !userRepository.existsById(recieverId)) {
            log.error(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
            throw new EntityNotFoundException(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
        }
    }

    private void heckForRequestsOfSixMonths(RecommendationRequestDto recommendationRequestDto) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository
                .findLatestPendingRequest(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getRecieverId());
        if (recommendationRequest.isPresent()) {
            LocalDateTime localDateTime = LocalDateTime.now().minus(COUNT_MONTHS, ChronoUnit.MONTHS);
            if (recommendationRequest.get().getUpdatedAt().isAfter(localDateTime)) {
                log.error(ExceptionMessages.RECOMMENDATION_FREQUENCY);
                throw new IllegalStateException(ExceptionMessages.RECOMMENDATION_FREQUENCY);
            }
        }
    }

    private List<SkillRequest> checkSkillRequestInDatabase(RecommendationRequestDto recommendationRequestDto) {
        List<Long> skillsId = recommendationRequestDto.getSkillsId();
        List<SkillRequest> skills = StreamSupport
                .stream(skillRequestRepository.findAllById(skillsId).spliterator(), false).toList();
        if (skills.isEmpty()) {
            log.error(ExceptionMessages.REQUEST_SKILL);
            throw new NullPointerException(ExceptionMessages.REQUEST_SKILL);
        }
        return skills;
    }
}