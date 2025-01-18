package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RecommendationRequestService {
    public static final int REQUEST_PERIOD_OF_THE_SAME_USER = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper mapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;


    public RecommendationRequestDto create(RecommendationRequestDto requestDto) {

        validateRecommendationRequest(requestDto);
        validateSkillIds(requestDto.getSkillIds());
        RecommendationRequest requestOrigEntity = mapper.toEntity(requestDto);
        User requester = getUserById(requestDto.getRequesterId());
        User receiver = getUserById(requestDto.getReceiverId());
        requestOrigEntity.setRequester(requester);
        requestOrigEntity.setReceiver(receiver);
        requestOrigEntity.setStatus(RequestStatus.PENDING);

        RecommendationRequest requestEntitySaved = recommendationRequestRepository.save(requestOrigEntity);

        List<SkillRequest> skills = new ArrayList<>();
        for (long skillId : requestDto.getSkillIds()) {
            SkillRequest skillRequest = skillRequestRepository.create(requestEntitySaved.getId(), skillId);
            if (skillRequest != null) {
                skills.add(skillRequest);
            }
        }
        requestEntitySaved.setSkills(skills);
        return mapper.toDto(recommendationRequestRepository.save(requestEntitySaved));
    }

    private void validateRecommendationRequest(RecommendationRequestDto request) {
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        Optional<RecommendationRequest> lastRequest = recommendationRequestRepository.findLatestPendingRequest(
                request.getRequesterId(), request.getReceiverId());

        if (lastRequest.isPresent()) {
            LocalDateTime lastRequestDate = lastRequest.get().getCreatedAt();
            if (lastRequestDate.isAfter(LocalDateTime.now().minusMonths(REQUEST_PERIOD_OF_THE_SAME_USER))) {
                throw new IllegalArgumentException("Recommendation request must be sent once in "
                        + REQUEST_PERIOD_OF_THE_SAME_USER + " months");
            }
        }
    }

    private void validateSkillIds(List<Long> skillIds) {
        for (long id : skillIds) {
            if (!skillRepository.existsById(id)) {
                throw new IllegalArgumentException("Skill with id = " + id + " not exist");
            }
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("User with id %s not found", id)));
    }
}
