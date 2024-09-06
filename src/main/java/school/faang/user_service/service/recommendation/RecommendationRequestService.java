package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;
import java.util.NoSuchElementException;
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
        if (!isUsersInDb(recommendationRequestDto)) {
            throw new NoSuchElementException("Requester id or receiver id is wrong");
        }
        if (!isSkillsInDb(recommendationRequestDto)) {
            throw new NoSuchElementException("No such skills in database");
        }

        List<SkillRequest> skillRequests = StreamSupport.stream(skillRequestRepository
                .findAllById(recommendationRequestDto.getSkillsIds()).spliterator(), false).toList();

        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.mapToEntity(recommendationRequestDto);
        recommendationRequestEntity.setSkills(skillRequests);
        recommendationRequestRepository.save(recommendationRequestEntity);
        return recommendationRequestMapper.mapToDto(recommendationRequestEntity);
    }

    private boolean isSkillsInDb(RecommendationRequestDto recommendationRequestDto) {
        List<SkillRequest> skillRequests = (List<SkillRequest>) skillRequestRepository.findAllById(recommendationRequestDto.getSkillsIds());
        List<Long> skillsIds = skillRequests.stream()
                .map(SkillRequest::getSkill)
                .map(Skill::getId)
                .toList();
        long existingSkillsCount = skillRepository.countExisting(skillsIds);
        return existingSkillsCount == skillRequests.size();
    }

    private boolean isUsersInDb(RecommendationRequestDto recommendationRequestDto) {
        if (!userRepository.existsById(recommendationRequestDto.getRequesterId())) {
            return false;
        } else if (!userRepository.existsById(recommendationRequestDto.getReceiverId())) {
            return false;
        }
        return true;
    }

}

//    public boolean checkUsers(RecommendationRequestDto recommendationRequestDto) {
//        List<Long> userIds = List.of(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId());
//        System.out.println(recommendationRequestDto.getRequesterId());
//        System.out.println(recommendationRequestDto.getReceiverId());
//
//        return userRepository.existsAllByIdIn(userIds);
//    }

//public boolean requestAllowed(RecommendationRequestDto recommendationRequestDto) {
//    long count = recommendationRequestRepository.findAll().stream()
//            .filter(request -> request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6)))
//            .filter(request -> request.getRequester().getId().equals(recommendationRequestDto.getRequesterId()))
//            .filter(request -> request.getReceiver().getId().equals(recommendationRequestDto.getReceiverId()))
//            .count();
//    return count == 0;
//}




