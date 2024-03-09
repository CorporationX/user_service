package school.faang.user_service.service.recommendation.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.handler.exception.EntityExistException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.util.recommendation.filters.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    @Override
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestValidator.validate(recommendationRequestDto);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        List<Long> skillIds = recommendationRequestDto.getSkillIds();

        if (skillIds != null && !skillIds.isEmpty()) {
            skillIds.forEach(skillId -> {
                skillRequestRepository.create(savedRecommendationRequest.getId(), skillId);
            });
        }

        return recommendationRequestMapper.toDto(savedRecommendationRequest);
    }

    @Override
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filters) {
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll();
        recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(recommendationRequests, filters));

        return recommendationRequestMapper.toDto(recommendationRequests);
    }

    @Override
    public RecommendationRequestDto getRequest(long id) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findById(id);

        recommendationRequestValidator.validateRequestForExist(recommendationRequest);

        return recommendationRequestMapper.toDto(recommendationRequest.orElse(null));
    }

    @Override
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findById(id);

        recommendationRequest.ifPresent(request -> {
            if (request.getStatus().equals(RequestStatus.REJECTED) || request.getStatus().equals(RequestStatus.ACCEPTED)) {
                throw new EntityExistException("Искомый запрос рекомендации уже принят или отклонен");
            }

            request.setStatus(RequestStatus.REJECTED);
            request.setRejectionReason(rejection.getReason());
            recommendationRequestRepository.save(request);
        });

        return recommendationRequestMapper.toDto(recommendationRequest.orElse(null));
    }
}
