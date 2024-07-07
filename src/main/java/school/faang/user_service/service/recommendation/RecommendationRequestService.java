package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static school.faang.user_service.entity.RequestStatus.PENDING;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    public final RecommendationRequestRepository recommendationRequestRepository;
    public final RecommendationRepository recommendationRepository;
    public final SkillRequestRepository skillRequestRepository;
    public final SkillRepository skillRepository;
    public final RecommendationRequestMapper recommendationRequestMapper;
    public final UserRepository userRepository;
    public final List<RequestFilter> requestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        validateRecommendationRequestDto(recommendationRequestDto);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto, userRepository);

        Long Id = recommendationRequestRepository.create(recommendationRequest.getRequester(),
                recommendationRequest.getReceiver(),
                recommendationRequest.getMessage());

        recommendationRequestDto.getSkills().forEach(skill -> skillRequestRepository.create(Id, skill.getId()));

        return recommendationRequestMapper.toDto(recommendationRequest, userRepository);
    }

    private void validateRecommendationRequestDto(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto.getMessage().isEmpty()) {
            throw new IllegalArgumentException("recommendation message can't be empty");
        }

        if (recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()).isEmpty()) {
            throw new EntityNotFoundException("requesterId and recieverId must be in the database");
        }

        LocalDateTime currentRequestTime = recommendationRequestDto.getCreatedAt();
        LocalDateTime latestRequestTime = recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()).get().getCreatedAt();

        if (ChronoUnit.MONTHS.between(currentRequestTime, latestRequestTime) > 6) {
            throw new IllegalArgumentException("Sorry, but you can create recommendation request only once every 6 months.\n" +
                    "Your latest recommendation request create time: " + latestRequestTime);
        }

        if (!recommendationRequestDto.getSkills().stream()
                .map(requestSkill -> requestSkill.getSkill().getTitle()).allMatch(skillRepository::existsByTitle)) {
            throw new IllegalArgumentException("Not all requested skill exists");
        }
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        Stream<RecommendationRequest> recommendations = StreamSupport.stream(recommendationRequestRepository.findAll().spliterator(), false);
        requestFilters.stream()
                .filter(requestFilter -> requestFilter.isApplicable(filter))
                .forEach(requestFilter -> requestFilter.apply(recommendations, filter));
        return recommendations.map(recommendation -> recommendationRequestMapper.toDto(recommendation, userRepository)).toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        if (recommendationRequestRepository.findById(id).isPresent()) {
            throw new NoSuchElementException("There is no recommendation request with id " + id);
        }
        return recommendationRequestMapper.toDto(recommendationRequestRepository.findById(id).get(), userRepository);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        if (recommendationRequestRepository.findById(id).isPresent()) {
            throw new NoSuchElementException("There is no recommendation request with id " + id);
        }

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id).get();

        if (recommendationRequest.getStatus() != PENDING) {
            throw new RuntimeException("Recommendation request with id " + id
                    + " already has a status: " + recommendationRequest.getStatus());
        }
        recommendationRequest.setRejectionReason(rejection.getReason());

        return recommendationRequestMapper.toDto(recommendationRequest, userRepository);
    }
}
