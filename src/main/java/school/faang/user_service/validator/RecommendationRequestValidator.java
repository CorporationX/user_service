package school.faang.user_service.validator;

import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.RecommendationRequestDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.RecommendationRequest;
import school.faang.user_service.model.entity.SkillRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.RecommendationRequestRepository;
import school.faang.user_service.repository.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;

    public void isRequestAllowed(RecommendationRequestDto recommendationRequestDto) {
        List<RecommendationRequest> existingCountRequests = recommendationRequestRepository.findAll().stream()
                .filter(request -> request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6)))
                .filter(request -> request.getRequester().getId().equals(recommendationRequestDto.getRequesterId()))
                .filter(request -> request.getReceiver().getId().equals(recommendationRequestDto.getReceiverId()))
                .toList();
        if (!existingCountRequests.isEmpty()) {
            throw new DuplicateRequestException("There is already a request created less than 6 months ago");
        }
    }

    public void isSkillsInDb(RecommendationRequestDto recommendationRequestDto) {
        List<SkillRequest> skillRequests = (List<SkillRequest>) skillRequestRepository.findAllById(recommendationRequestDto.getSkillsIds());
        List<Long> skillsIds = skillRequests.stream()
                .map(SkillRequest::getSkill)
                .map(Skill::getId)
                .toList();
        long existingSkillsCount = skillRepository.countExisting(skillsIds);
        if (existingSkillsCount != recommendationRequestDto.getSkillsIds().size()) {
            throw new NoSuchElementException("No such skills in database");
        }
    }

    public void isUsersInDb(RecommendationRequestDto recommendationRequestDto) {
        if (!userRepository.existsById(recommendationRequestDto.getRequesterId()) ||
                !userRepository.existsById(recommendationRequestDto.getReceiverId())) {
            throw new NoSuchElementException("Requester id or receiver id is wrong");
        }
    }
}
