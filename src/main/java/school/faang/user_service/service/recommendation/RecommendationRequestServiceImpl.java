package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;
    private final RecommendationRequestRepository repository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
//        Long receiverId = recommendationRequestDto.getReceiverId();
//        Long requesterId = recommendationRequestDto.getRequesterId();
//        boolean existsReceiver = userRepository.existsById(receiverId);
//        boolean existsRequester = userRepository.existsById(requesterId);
//        boolean equalsReceiverAndRequester = receiverId.equals(requesterId);
        recommendationRequestValidator.validateRequesterAndReceiver(recommendationRequestDto);
        recommendationRequestValidator.validateRequestAndCheckTimeLimit(recommendationRequestDto);
        recommendationRequestValidator.validateSkillRequest(recommendationRequestDto);
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);
        recommendationRequest.getSkills()
                .forEach(skill -> skillRequestRepository.create(recommendationRequestDto.getId(), skill.getId()));
        RecommendationRequest createRequest = repository.save(recommendationRequest);
        return recommendationRequestMapper.toDto(createRequest);
    }

    @Override
    @Transactional
    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found RequestRecommendation for id: " + id));
        return recommendationRequestMapper.toDto(request);
    }

    @Override
    @Transactional
    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejectionDto) throws DataValidationException {
        RecommendationRequestDto recommendationRequestDto = getRequest(id);
        if (recommendationRequestDto.getStatus() == RequestStatus.PENDING) {
            RecommendationRequest request = recommendationRequestMapper.toEntity(recommendationRequestDto);
            request.setStatus(RequestStatus.REJECTED);
            request.setRejectionReason(rejectionDto.getReason());
            return recommendationRequestMapper.toDto(request);
        } else {
            throw new DataValidationException("It is impossible to refuse a request that is not in a pending state");
        }
    }

    @Override
    @Transactional
    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto filters) {
        Stream<RecommendationRequest> recommendationRequests = repository.findAll().stream();
        return recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(recommendationRequests, filters))
                .map(recommendationRequestMapper::toDto)
                .toList();
    }
}
