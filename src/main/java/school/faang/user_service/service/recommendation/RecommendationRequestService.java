package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static int SIX_MONTHS = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        Optional<User> requester = userRepository.findById(recommendationRequestDto.getRequesterId());
        Optional<User> receiver = userRepository.findById(recommendationRequestDto.getReceiverId());

        if (
                requester.isPresent() &&
                receiver.isPresent() &&
                canRequestRecommendation(requester.get(), receiver.get()) &&
                allSkillsExist(recommendationRequestDto.getSkills())
        ) {
            RecommendationRequest toCreateRequest =
                    recommendationRequestMapper.toRecommendationRequest(recommendationRequestDto);
            recommendationRequestRepository.save(toCreateRequest);
            toCreateRequest
                    .getSkills()
                    .forEach(skill ->
                            skillRequestRepository.create(toCreateRequest.getId(), skill.getId()));
            return recommendationRequestMapper.toRecommendationRequestDto(toCreateRequest);
        }

        return null;
    }

    private boolean canRequestRecommendation(User requester, User receiver) {
        Optional<RecommendationRequest> request =
                recommendationRequestRepository.findLatestPendingRequest(requester.getId(), receiver.getId());
        if (request.isPresent()) {
            LocalDate lastRequestDate = request.get().getCreatedAt().toLocalDate();
            LocalDate currentDate = LocalDate.now();
            long months = ChronoUnit.MONTHS.between(lastRequestDate, currentDate);
            return months >= SIX_MONTHS;
        } else {
            return true;
        }
    }

    private boolean allSkillsExist(List<Skill> skills) {
        return skills
                .stream()
                .allMatch(skill -> skillRepository.existsById(skill.getId()));
    }
}
