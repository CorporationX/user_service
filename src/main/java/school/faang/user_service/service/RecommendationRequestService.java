package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.controller.recommendation.RecommendationRequestDto;
import school.faang.user_service.controller.recommendation.RejectionDto;
import school.faang.user_service.controller.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
//import school.faang.user_service.mapper.recommendation.RecommendationRequestFilterMapper;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.mapper.recommendation.RecommendationRequestRejectionMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.util.filter.Filter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int REQUESTS_PERIOD_RESTRICTION = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
//    private final RecommendationRequestFilterMapper recommendationRequestFilterMapper;
    private final RecommendationRequestRejectionMapper recommendationRequestRejectionMapper;
    private final List<Filter<RequestFilterDto, RecommendationRequest>> filters;

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

    public List<RecommendationRequestDto> getRequestsByFilter(RequestFilterDto filterDto) {
        Stream<RecommendationRequest> recommendationRequestsStream = recommendationRequestRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(recommendationRequestsStream, (stream, filter) -> filter.apply(stream, filterDto),
                        ((subGenStream, stream) -> stream))
                .distinct().map(recommendationRequestMapper::toDto)
                .toList();
    }

    @Transactional
    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.getReferenceById(id);
        if (recommendationRequest.getId()<=0){
            throw new DataValidationException("Haven't found recommendation request");
        }
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public RejectionDto rejectRequest(long id, RejectionDto rejectionDto) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findById(id);
        if (recommendationRequest.isPresent()) {
            if (recommendationRequest.get().getStatus() == RequestStatus.PENDING) {
                recommendationRequest.get().setStatus(RequestStatus.REJECTED);
                recommendationRequest.get().setRejectionReason(rejectionDto.getReason());
            }
        } else {
            throw new DataValidationException("Can't find recommendation request by ID");
        }
        return recommendationRequestRejectionMapper.toDto(recommendationRequestRepository.save(recommendationRequest.get()));
    }

    private void validateReceiverExistence(RecommendationRequestDto requestDto) {
        if (!recommendationRequestRepository.existsById(requestDto.getReceiverId())) {
            throw new DataValidationException("We haven't managed to find receiver in DataBase");
        }
    }

    private void validateRequesterExistence(RecommendationRequestDto requestDto) {
        if (!recommendationRequestRepository.existsById(requestDto.getRequesterId())) {
            throw new DataValidationException("We haven't managed to find requester in DataBase");
        }
    }

    private void validateRequestsPeriod(RecommendationRequestDto requestDto) {
        LocalDateTime createdDate = requestDto.getCreatedAt();
        LocalDateTime updatedDate = requestDto.getUpdatedAt();
        long monthsBetweenRequests = ChronoUnit.MONTHS.between(createdDate, updatedDate);
        if (monthsBetweenRequests <= REQUESTS_PERIOD_RESTRICTION) {
            String message = ("You can't send request for " + (REQUESTS_PERIOD_RESTRICTION - monthsBetweenRequests) + " days");
            throw new DataValidationException(message);
        }
    }

    private void validateSkillsExistence(RecommendationRequestDto requestDto) {
        List<Long> skillsRequestIds = requestDto.getSkillsIds();
        List<SkillRequest> existedSkillsRequestIds = skillRequestRepository.findAllById(skillsRequestIds);
        if (skillsRequestIds.size() != existedSkillsRequestIds.size()) {
            throw new DataValidationException("One of skills have not been found in DataBase");
        }
    }
}