package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filer.RequestFilter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
        verificationObject(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getRecieverId());

        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository
                .findLatestPendingRequest(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getRecieverId());
        if (recommendationRequest.isPresent()) {
            LocalDateTime localDateTime = LocalDateTime.now().minus(COUNT_MONTHS, ChronoUnit.MONTHS);
            if (recommendationRequest.get().getUpdatedAt().isAfter(localDateTime)) {
                throw new IllegalArgumentException("Recommendation request can only be sent once every 6 months");
            }
        }

        List<Long> skillsId = recommendationRequestDto.getSkillsId();
        List<SkillRequest> skills = StreamSupport.stream(skillRequestRepository.findAllById(skillsId).spliterator(), false).toList();
        if (skills.isEmpty()) {
            throw new NullPointerException("One or more requested skills do not exist in the database");
        }

        RecommendationRequest newRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);
        recommendationRequestRepository.save(newRequest);

        skills.forEach(skill -> skillRequestRepository.create(newRequest.getId(), skill.getId()));

        return recommendationRequestMapper.toDto(newRequest);
    }

    //    @Transactional(readOnly = true)
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

    //    @Transactional(readOnly = true)
    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = recommendationRequestRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Request with ID " + id + " not found"));
        return recommendationRequestMapper.toDto(request);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        return recommendationRequestRepository.findById(id)
                .map(request -> {
                    if (request.getStatus() == RequestStatus.PENDING) {
                        request.setStatus(RequestStatus.REJECTED);
                        request.setRejectionReason(rejectionDto.getReason());
                        recommendationRequestRepository.save(request);
                        return recommendationRequestMapper.toDto(request);
                    } else {
                        throw new IllegalArgumentException("The status is not PENDING");
                    }
                }).orElseThrow(() -> new IllegalArgumentException("Request not found"));
    }

    private void verificationObject(long requesterId, long recieverId) {
        if (!userRepository.existsById(requesterId) || !userRepository.existsById(recieverId)) {
            throw new IllegalArgumentException("Requester or Reciever does not exist in the database");
        }
    }
}