package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RequestNotFoundException;
import school.faang.user_service.exception.RequestTimeOutException;
import school.faang.user_service.exception.SkillsNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.FilterRecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<FilterRecommendationRequest> filterRecommendationRequestList;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getRequesterId() == null || recommendationRequest.getReceiverId() == null)
            throw new UserNotFoundException("User not found");
        if (recommendationRequest.getCreatedAt().plusMonths(6).isAfter(recommendationRequest.getUpdatedAt())) {
            throw new RequestTimeOutException("Last request was less than 6 months ago");
        }

        try {
            recommendationRequest.getSkills()
                    .forEach(skillRequestId -> {
                        skillRequestRepository.existsById(skillRequestId.getSkillId());
                    });
        } catch (NullPointerException e) {
            throw new SkillsNotFoundException("Skills not found");
        }
        recommendationRequest.getSkills()
                .forEach(skillRequestId -> skillRequestRepository.create(skillRequestId.getId(), skillRequestId.getSkillId()));

        return recommendationRequestMapper
                .toDto(recommendationRequestRepository.create(recommendationRequest.getRequesterId(), recommendationRequest.getReceiverId(), recommendationRequest.getMessage()));
    }

    public RecommendationRequestDto getRequest(long id) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(id);
        return recommendationRequestMapper.toDto(request.orElseThrow(() -> new RequestNotFoundException("Request not found by id: " + id)));
    }

    public List<RecommendationRequestDto> getRequest(RequestFilterDto filter) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();
        filterRecommendationRequestList.stream()
                .forEach(filterRecommendationRequest -> {
                    if (filterRecommendationRequest.isApplicable(filter))
                        filterRecommendationRequest.apply(recommendationRequests, filter);
                });
        return recommendationRequestMapper.toDto(recommendationRequests.toList());
    }

}

