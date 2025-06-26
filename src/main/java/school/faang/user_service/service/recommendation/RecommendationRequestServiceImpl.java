package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.kafka.UserDtoNotification;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterStrategy;
import school.faang.user_service.kafka.events.RecommendationRequestEvent;
import school.faang.user_service.kafka.producer.KafkaDataSenderImpl;
import school.faang.user_service.kafka.producer.KafkaTopics;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final UserContext userContext;

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;

    private final List<RecommendationRequestFilterStrategy> recommendationRequestFilters;

    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserMapper userMapper;

    private final KafkaDataSenderImpl kafkaDataSender;
    private final KafkaTopics kafkaTopics;

    @Override
    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        log.info("Creating recommendation request for receiver: {}", recommendationRequestDto.getReceiverId());
        recommendationRequestDto.setRequesterId(userContext.getUserId());
        User requester = userRepository.findById(recommendationRequestDto.getRequesterId())
                .orElseThrow(() -> {
                    log.error("Requester with id {} was not found", recommendationRequestDto.getRequesterId());
                    return new EntityNotFoundException("Requester with id %s was not found"
                            .formatted(recommendationRequestDto.getRequesterId()));
                });
        User receiver = userRepository.findById(recommendationRequestDto.getReceiverId())
                .orElseThrow(() -> {
                    log.error("Receiver with id {} was not found", recommendationRequestDto.getReceiverId());
                    return new EntityNotFoundException("Receiver with id %s was not found"
                            .formatted(recommendationRequestDto.getReceiverId()));
                });
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findLatestPendingRequest(
                        recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId())
                .orElse(recommendationRequestMapper.toEntity(recommendationRequestDto));

        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);

        if (recommendationRequestRepository.existsById(recommendationRequest.getId())
                && recommendationRequest.getUpdatedAt() != null
                && recommendationRequest.getUpdatedAt().isAfter(LocalDateTime.now().minus(Period.ofMonths(6)))) {
            log.warn("Recommendation request from user {} to {} has already been updated in the last 6 months",
                    requester.getId(), receiver.getId());
            throw new IllegalArgumentException("Recommendation request has already been updated in the last 6 months.");
        }
        List<Skill> skills = skillRepository.findAllById(recommendationRequestDto.getSkillIds());
        if (skills.isEmpty()) {
            log.error("Not all required skills with ids {} exist in data base", recommendationRequestDto.getSkillIds());
            throw new EntityNotFoundException("Not all required skills exist in data base");
        }
        log.debug("Saving recommendation request from user {} to user {}", requester.getId(), receiver.getId());
        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        savedRecommendationRequest.setSkills(
                skills.stream()
                        .map(skill -> {
                            SkillRequest skillRequest = new SkillRequest();
                            skillRequest.setRequest(savedRecommendationRequest);
                            skillRequest.setSkill(skill);
                            return skillRequestRepository.save(skillRequest);
                        })
                        .toList());
        UserDtoNotification authorDto = userMapper.toDtoNotification(requester);
        UserDtoNotification receiverDto = userMapper.toDtoNotification(receiver);
        RecommendationRequestEvent event = new RecommendationRequestEvent(savedRecommendationRequest.getId());
        event.setAuthor(authorDto);
        event.setReceiver(receiverDto);
        log.info("Sending recommendation request event for request id {}", savedRecommendationRequest.getId());
        kafkaDataSender.send(
                kafkaTopics.getRecommendationRequestTopic(),
                event
        );
        log.info("Recommendation request from user {} to {} created successfully", requester.getId(), receiver.getId());
        return recommendationRequestMapper.toDto(savedRecommendationRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        Stream<RecommendationRequest> recommendations = recommendationRequestRepository.findAll().stream();

        for (RecommendationRequestFilterStrategy recommendationRequestFilter : recommendationRequestFilters) {
            if (recommendationRequestFilter.isApplicable(filter)) {
                recommendations = recommendationRequestFilter.apply(recommendations, filter);
            }
        }

        return recommendations.map(recommendationRequestMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper.toDto(recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation request with id %s doesn't exist"
                        .formatted(id))));
    }

    @Override
    @Transactional
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        log.info("Rejecting recommendation request with id: {}", id);
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Recommendation request with id {} doesn't exist", id);
                    return new EntityNotFoundException("Recommendation request with id %s doesn't exist".formatted(id));
                });

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            log.warn("Unable to reject request with id {} because its status is not PENDING", id);
            throw new IllegalArgumentException("Unable to reject request");
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());

        log.debug("Saving rejected recommendation request with id: {}", id);
        recommendationRequestRepository.save(recommendationRequest);

        log.info("Recommendation request with id {} was successfully rejected", id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }
}
