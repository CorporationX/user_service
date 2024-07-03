package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    public final RecommendationRequestRepository recommendationRequestRepository;
    public final RecommendationRepository recommendationRepository;
    public final SkillRequestRepository skillRequestRepository;
    public final SkillRepository skillRepository;
    public final RecommendationRequestMapper recommendationRequestMapper;
    public final UserRepository userRepository;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()).isEmpty()) {
            log.error("requesterId or recieverId is not in the database");
            throw new IllegalArgumentException("requesterId and recieverId must be in the database");
        }

        LocalDateTime currentRequestTime = recommendationRequestDto.getCreatedAt();
        LocalDateTime latestRequestTime = recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()).get().getCreatedAt();

        if (ChronoUnit.MONTHS.between(currentRequestTime, latestRequestTime) > 6) {
            log.error("request time difference less than 6 months");
            throw new IllegalArgumentException("Sorry, but you can create recommendation request only once every 6 months.\n" +
                    "Your latest recommendation request create time: " + latestRequestTime);
        }

        if (!recommendationRequestDto.getSkills().stream()
                .map(requestSkill -> requestSkill.getSkill().getTitle()).allMatch(skillRepository::existsByTitle)) {
            log.error("not all requested skill exists in database");
            throw new IllegalArgumentException("Not all requested skill exists");
        }

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto, userRepository);

        Long Id = recommendationRequestRepository.create(recommendationRequest.getRequester(),
                recommendationRequest.getReceiver(),
                recommendationRequest.getMessage());

        recommendationRequestDto.getSkills().forEach(skill -> skillRequestRepository.create(Id, skill.getId()));

        return recommendationRequestMapper.toDto(recommendationRequest, userRepository);
    }
}
