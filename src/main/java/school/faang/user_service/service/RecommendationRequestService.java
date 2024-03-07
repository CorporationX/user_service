package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.RejectFailException;
import school.faang.user_service.filter.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestMapper skillRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilterList;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        validate(recommendationRequest);

        AtomicInteger skillsCheck = new AtomicInteger();

        recommendationRequest.getSkills()
                .forEach(skillRequestId -> {
                    if (!skillRequestRepository.existsById(skillRequestId.getSkillId()))
                        skillsCheck.getAndIncrement();
                });

        if (skillsCheck.longValue() > 0)
            throw new DataValidationException("Skills not found");

        recommendationRequest.getSkills()
                .forEach(skillRequestId -> skillRequestRepository.create(skillRequestId.getId(), skillRequestId.getSkillId()));

        recommendationRequestRepository
                .create(recommendationRequest.getRequesterId(), recommendationRequest.getReceiverId(), recommendationRequest.getMessage());

        return recommendationRequest;
    }

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper
                .toDto(findRequestById(id)
                        .orElseThrow(() -> new DataValidationException("Request not found by id: " + id)));
    }

    public List<RecommendationRequestDto> getRequest(RequestFilterDto filter) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();
        for (RecommendationRequestFilter recommendationRequestFilter : recommendationRequestFilterList) {
            if (recommendationRequestFilter.isApplicable(filter))
                recommendationRequests = recommendationRequestFilter.apply(recommendationRequests, filter);
        }
        List<RecommendationRequestDto> recommendationRequestDtos = new ArrayList<>();
        for (RecommendationRequest requests : recommendationRequests.toList()) {
            recommendationRequestDtos.add(recommendationRequestMapper.toDto(requests));
        }
        return recommendationRequestDtos;
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest request = findRequestById(id).orElseThrow(() -> new DataValidationException("Request not found by id: " + id));
        if (request.getStatus().equals(RequestStatus.PENDING)) {
            request.setRejectionReason(rejection.getReason());
            request.setStatus(RequestStatus.REJECTED);
            recommendationRequestRepository.save(request);
        } else {
            throw new RejectFailException("Request is accepted or rejected");
        }
        return recommendationRequestMapper.toDto(request);
    }

    private Optional<RecommendationRequest> findRequestById(long id) {
        return recommendationRequestRepository.findById(id);
    }

    private void validate(RecommendationRequestDto recommendationRequest) {
        if (!userRepository.existsById((recommendationRequest.getRequesterId())) || !userRepository.existsById(recommendationRequest.getReceiverId()))
            throw new DataValidationException("User not found");
        if (recommendationRequest.getSkills() == null)
            throw new DataValidationException("skills is null");
    }
}

