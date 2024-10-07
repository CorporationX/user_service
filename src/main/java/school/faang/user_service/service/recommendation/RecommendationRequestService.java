package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.RequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {
    private static final String RECOMMENDATION_REQUEST_NOT_FOUND_MESSAGE = "No recommendation request exist with id ";
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserService userService;
    private final SkillService skillService;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final SkillValidator skillValidator;
    private final List<RequestFilter> requestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        Optional<RecommendationRequest> lastRequest = getLastPendingRequest(recommendationRequestDto);
        lastRequest.ifPresent(recommendationRequestValidator::validatePreviousRequest);
        User requester = userService.getUserById(recommendationRequestDto.getRequesterId());
        User receiver = userService.getUserById(recommendationRequestDto.getReceiverId());
        List<Skill> skills = skillService.getAllSkills(recommendationRequestDto.getSkillIds());
        skillValidator.validateSkillsExist(recommendationRequestDto.getSkillIds(), skills);
        RecommendationRequest rq = recommendationRequestMapper.toEntity(recommendationRequestDto);
        rq.setRequester(requester);
        rq.setReceiver(receiver);
        List<SkillRequest> skillRequests = skills.stream()
                .map(skill -> new SkillRequest(skill.getId(), rq, skill))
                .toList();
        rq.setSkills(skillRequests);
        RecommendationRequest requestSavedResult = recommendationRequestRepository.save(rq);

        return recommendationRequestMapper.toDto(requestSavedResult);
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto filters) {
        Stream<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream();
        return requestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.applyFilter(requests, filters))
                .map(recommendationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest rqToFind = findExistingRecommendationRequest(id);
        return recommendationRequestMapper.toDto(rqToFind);
    }

    public RecommendationRequestDto rejectRequest(long id, RecommendationRejectionDto rejection) {
        RecommendationRequest rq = findExistingRecommendationRequest(id);
        recommendationRequestValidator.validateRequestStatus(rq);
        rq.setRejectionReason(rejection.getReason());
        rq.setStatus(RequestStatus.REJECTED);
        recommendationRequestRepository.save(rq);
        return recommendationRequestMapper.toDto(rq);
    }

    private Optional<RecommendationRequest> getLastPendingRequest(RecommendationRequestDto recommendationRequestDto) {
        return recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId());
    }

    private RecommendationRequest findExistingRecommendationRequest(long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(() ->
                new DataValidationException(RECOMMENDATION_REQUEST_NOT_FOUND_MESSAGE + id));
    }
}
