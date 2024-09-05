package school.faang.user_service.service.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;



    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        if (checkUsers(recommendationRequestDto)) {
            if (checkSkillsInDb(recommendationRequestDto)) {
                if (requestAllowed(recommendationRequestDto)) {
                    RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.mapToEntity(recommendationRequestDto);
                    List<SkillRequest> skillRequests = StreamSupport.stream(skillRequestRepository.findAllById(recommendationRequestDto.getSkillsId()).spliterator(), false).toList();
                    recommendationRequestEntity.setSkills(skillRequests);
                    recommendationRequestEntity = recommendationRequestRepository.save(recommendationRequestEntity);
                    recommendationRequestEntity.getSkills()
                            .forEach(skillRequest -> skillRequestRepository.create(recommendationRequestDto.getRequesterId(), skillRequest.getId()));
                    return recommendationRequestMapper.mapToDto(recommendationRequestEntity);
                } else {
                    throw new IllegalArgumentException("Request not allowed, there is already such request created less than 6 months ago");
                }
            } else {
                throw new NoSuchElementException("No such skills in database");
            }
        } else {
            throw new IllegalArgumentException("Requester id or receiver id is wrong");
        }
    }

    private boolean checkUsers(RecommendationRequestDto recommendationRequestDto) {
        System.out.println("sdfasdfasdfasdfasd");
        System.out.println(userRepository.count());
        System.out.println(recommendationRequestDto.getRequesterId());
        System.out.println(recommendationRequestDto.getReceiverId());
        Optional<User> requester = userRepository.findById(recommendationRequestDto.getRequesterId());
        System.out.println(requester.get());
        Optional<User> receiver = userRepository.findById(recommendationRequestDto.getRequesterId());
        receiver.get();
        System.out.println(requester.isPresent() + " - " + receiver.isPresent());
        return requester.isPresent() && receiver.isPresent();
    }

    private boolean requestAllowed(RecommendationRequestDto recommendationRequestDto) {
        long count = recommendationRequestRepository.findAll().stream()
                .filter(request -> request.getCreatedAt().isAfter(LocalDateTime.now().minus(6, ChronoUnit.MONTHS)))
                .filter(request -> request.getRequester().getId().equals(recommendationRequestDto.getRequesterId()))
                .filter(request -> request.getReceiver().getId().equals(recommendationRequestDto.getReceiverId()))
                .count();
        return count == 0;
    }

    private boolean checkSkillsInDb(RecommendationRequestDto recommendationRequestDto) {
        List<SkillRequest> skillRequests = (List<SkillRequest>) skillRequestRepository.findAllById(recommendationRequestDto.getSkillsId());
        List<Skill> skills = skillRequests.stream()
                .map(skillRequest -> skillRequest.getSkill())
                .toList();
        System.out.println(skillRepository.existsAllByIdIn(skills));
        return skillRepository.existsAllByIdIn(skills);
    }
}
