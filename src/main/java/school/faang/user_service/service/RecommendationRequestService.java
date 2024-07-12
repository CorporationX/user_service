package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.controller.recommendation.RecommendationRequestDto;
import school.faang.user_service.controller.recommendation.RejectionDto;
import school.faang.user_service.controller.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationRequestFilterMapper;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.RecommendationRequestRejectionMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int REQUESTS_PERIOD_RESTRICTION = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestFilterMapper recommendationRequestFilterMapper;
    private final RecommendationRequestRejectionMapper recommendationRequestRejectionMapper;
    private final List<RecommendationRequestFilter> filters;

    public RecommendationRequestDto create(RecommendationRequestDto requestDto) {
        validateReceiverExistence(requestDto);
        validateRequesterExistence(requestDto);
        validateRequestsPeriod(requestDto);
        validateSkillsExistence(requestDto);

        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.ToEntity(requestDto);
        User requester = recommendationRequestRepository.getReferenceById(requestDto.getRequesterId()).getRequester();
        User receiver = recommendationRequestRepository.getReferenceById(requestDto.getReceiverId()).getReceiver();
        recommendationRequestEntity.setRequester(requester);
        recommendationRequestEntity.setReceiver(receiver);
        recommendationRequestEntity.setSkills(skillRequestRepository.findAllById(requestDto.getSkillsIds()));
        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(recommendationRequestEntity));
    }

    public List<RequestFilterDto> getRequestsByFilter(RequestFilterDto filterDto) {
        Stream<RecommendationRequest> recommendationRequestsStream = recommendationRequestRepository.findAll().stream();
        filters.stream().filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(recommendationRequestsStream, filterDto));
        RecommendationRequest recommendationRequest = recommendationRequestFilterMapper.ToEntity(filterDto);
        List<RecommendationRequest> recommendationRequests = recommendationRequestsStream.toList();
        return recommendationRequests.stream().map(recommendationRequestFilterMapper::toDto).toList();
    }

    public RecommendationRequestDto getRequest(Long id) {
        validateRequest(id);
        RecommendationRequest recommendationRequest = recommendationRequestRepository.getReferenceById(id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    private boolean validateRequest(Long id) {
        if (!recommendationRequestRepository.existsById(id)) {
            throw new RuntimeException("Can't find Recommendation request by ID");
        }
        return true;
    }

    public RejectionDto rejectRequest(long id, RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).get();
        if (recommendationRequest == null) {
            throw new RuntimeException("Can't find recommendation request by ID");
        }
        RecommendationRequest recommendationRequestEntity = recommendationRequestRejectionMapper.ToEntity(rejectionDto);
        if (recommendationRequest.getStatus() == RequestStatus.PENDING) {
            recommendationRequestEntity.setStatus(RequestStatus.REJECTED);
            recommendationRequestEntity.setRejectionReason(rejectionDto.getReason());
        }
        return recommendationRequestRejectionMapper.toDto(recommendationRequestRepository.save(recommendationRequestEntity));
    }

    private void validateReceiverExistence(RecommendationRequestDto requestDto) {
        if (!recommendationRequestRepository.existsById(requestDto.getReceiverId())) {
            throw new RuntimeException("We haven't managed to find receiver in DataBase");
        }
    }

    private void validateRequesterExistence(RecommendationRequestDto requestDto) {
        if (!recommendationRequestRepository.existsById(requestDto.getRequesterId())) {
            throw new RuntimeException("We haven't managed to find requester in DataBase");
        }
    }

    private void validateRequestsPeriod(RecommendationRequestDto requestDto) {
        LocalDateTime createdDate = requestDto.getCreatedAt();
        LocalDateTime updatedDate = requestDto.getUpdatedAt();
        long monthsBetweenRequests = ChronoUnit.MONTHS.between(createdDate, updatedDate);
        if (monthsBetweenRequests <= REQUESTS_PERIOD_RESTRICTION) {
            String message = ("You can't send request for " + (REQUESTS_PERIOD_RESTRICTION - monthsBetweenRequests) + " days");
            throw new RuntimeException(message);
        }
    }

    private void validateSkillsExistence(RecommendationRequestDto requestDto) {
        List<Long> skillsRequestIds = requestDto.getSkillsIds();
        List<SkillRequest> existedSkillsRequestIds = skillRequestRepository.findAllById(skillsRequestIds);
//        List<Long> existedSkillsRequestIds = skillsRequestIds.stream().filter(skillRequestRepository::existsById).toList();
        if (skillsRequestIds.size() != existedSkillsRequestIds.size()) {
            throw new RuntimeException("One of skills have not been found in DataBase");
        }
    }
}