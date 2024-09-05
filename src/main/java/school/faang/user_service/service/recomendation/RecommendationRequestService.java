package school.faang.user_service.service.recomendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;

    @Autowired
    public RecommendationRequestService(RecommendationRequestRepository recommendationRequestRepository, UserRepository userRepository, RecommendationRequestMapper recommendationRequestMapper, SkillRequestRepository skillRequestRepository, SkillRepository skillRepository) {
        this.recommendationRequestRepository = recommendationRequestRepository;
        this.userRepository = userRepository;
        this.recommendationRequestMapper = recommendationRequestMapper;
        this.skillRequestRepository = skillRequestRepository;
        this.skillRepository = skillRepository;
    }

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        Optional<User> requesterId = userRepository.findById(recommendationRequestDto.getRequesterId());
        Optional<User> receiverId = userRepository.findById(recommendationRequestDto.getRequesterId());
        if (requesterId.isPresent() && receiverId.isPresent()) {
            if (skillRepository.existsAllByIdIn(recommendationRequestDto.getSkillsId())) {
                RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.mapToEntity(recommendationRequestDto);
                List<SkillRequest> skillRequests = StreamSupport.stream(skillRequestRepository.findAllById(recommendationRequestDto.getSkillsId()).spliterator(), false).toList();
                recommendationRequestEntity.setSkills(skillRequests);
                recommendationRequestEntity = recommendationRequestRepository.save(recommendationRequestEntity);
                recommendationRequestEntity.getSkills()
                        .forEach(skillRequest -> skillRequestRepository.create(recommendationRequestDto.getRequesterId(), skillRequest.getId()));
                return recommendationRequestMapper.mapToDto(recommendationRequestEntity);
            } else {
                throw new NoSuchElementException("No such skills in database");
            }
        } else {
            throw new IllegalArgumentException("Requester id or receiver id is wrong");
        }
    }
}
