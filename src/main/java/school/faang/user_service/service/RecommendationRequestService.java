package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.event.RecommendationRequestEvent;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.publisher.RecommendationRequestEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestService skillRequestService;
    private final UserService userService;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final List<Filter<RecommendationRequest, RequestFilterDto>> filters;
    private final RecommendationRequestEventPublisher recommendationRequestEventPublisher;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        User requester = userService.getUserById(dto.getRequesterId())
                .orElseThrow(() -> new EntityNotFoundException("Requester with ID " + dto.getRequesterId() + " not found"));
        User receiver = userService.getUserById(dto.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver with ID " + dto.getRequesterId() + " not found"));

        recommendationRequestValidator.validateUsersExistence(requester, receiver);
        recommendationRequestValidator.validateRequestFrequency(dto.getRequesterId(), dto.getReceiverId());
        recommendationRequestValidator.validateSkillsExistence(dto.getSkillIdentifiers());

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dto);

        recommendationRequest.setSkills(Optional.ofNullable(recommendationRequest.getSkills()).orElse(new ArrayList<>()));

        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        RecommendationRequest savedRequest = recommendationRequestRepository.save(recommendationRequest);

        if (dto.getSkillIdentifiers() != null && !dto.getSkillIdentifiers().isEmpty()) {
            List<Skill> skills = skillRequestService.getSkillsByIds(dto.getSkillIdentifiers());
            List<SkillRequest> skillRequests = skillRequestService.createSkillRequests(skills, savedRequest);
            savedRequest.getSkills().addAll(skillRequests);
            recommendationRequestRepository.save(savedRequest);
        }

        recommendationRequestEventPublisher.publish(
                RecommendationRequestEvent.builder()
                        .requestId(savedRequest.getId())
                        .receiverId(savedRequest.getReceiver().getId())
                        .requesterId(savedRequest.getRequester().getId())
                        .build()
        );

        return recommendationRequestMapper.toDto(savedRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        Stream<RecommendationRequest> stream = recommendationRequestRepository.findAll().stream();

        for (Filter<RecommendationRequest, RequestFilterDto> filter : filters) {
            if (filter.isApplicable(filterDto)) {
                stream = filter.apply(stream, filterDto);
            }
        }

        return stream
                .map(recommendationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest request = recommendationRequestValidator.validateAndGetRecommendationRequest(id);

        return recommendationRequestMapper.toDto(request);
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejection) {
        RecommendationRequest request = recommendationRequestValidator.validateAndGetRecommendationRequest(id);

        recommendationRequestValidator.validateRejectRequest(request);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());

        recommendationRequestRepository.save(request);

        return recommendationRequestMapper.toDto(request);
    }
}
