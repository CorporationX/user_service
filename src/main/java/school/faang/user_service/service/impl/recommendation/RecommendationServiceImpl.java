package school.faang.user_service.service.impl.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.recommendation.RecommendationDto;
import school.faang.user_service.model.dto.recommendation.SkillOfferDto;
import school.faang.user_service.model.entity.recommendation.Recommendation;
import school.faang.user_service.event.RecommendationEventDto;
import school.faang.user_service.event.RecommendationEventDto;

import school.faang.user_service.model.dto.recommendation.RecommendationDto;
import school.faang.user_service.model.dto.recommendation.SkillOfferDto;
import school.faang.user_service.model.entity.recommendation.Recommendation;
import school.faang.user_service.model.event.RecommendationReceivedEvent;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.publisher.RecommendUserPublisher;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.recommendation.RecommendationServiceValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationServiceValidator validator;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;
    private final UserContext userContext;
    private final RecommendUserPublisher recommendUserPublisher;

    @Override
    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        validator.validateRecommendation(recommendation);
        validator.validateDaysBetweenRecommendations(recommendation);
        validator.validateSkillOffers(recommendation);

        recommendation.skillOffers()
                .stream()
                .map(SkillOfferDto::skillId)
                .filter(skillId -> skillRepository
                        .findUserSkill(skillId, recommendation.receiverId()).isEmpty())
                .forEach(skillId -> skillOfferRepository.create(skillId, recommendation.id()));

        sendEvent(recommendation);
        sendRecommendUserEvent(recommendation);
        return recommendation;
    }

    @Override
    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        validator.validateRecommendation(recommendationDto);
        validator.validateDaysBetweenRecommendations(recommendationDto);
        validator.validateSkillOffers(recommendationDto);

        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.id());

        recommendationDto
                .skillOffers()
                .forEach(skillOfferDto -> skillOfferRepository
                        .create(skillOfferDto.skillId(), recommendationDto.id()));

        return recommendationDto;
    }

    @Override
    @Transactional
    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    @Override
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(receiverId, Pageable.unpaged());

        return recommendations.map(recommendationMapper::toDto).toList();
    }

    @Override
    public List<RecommendationDto> getAllGivenRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByAuthorId(receiverId, Pageable.unpaged());

        return recommendations.map(recommendationMapper::toDto).toList();
    }

    private void sendRecommendUserEvent(RecommendationDto recommendationDto) {
        RecommendationEventDto event = RecommendationEventDto
                .builder()
                .authorId(recommendationDto.authorId())
                .receiverId(recommendationDto.receiverId())
                .recommendedAt(LocalDateTime.now())
                .build();
        recommendUserPublisher.publish(event);
    }

    private void sendEvent(RecommendationDto recommendation) {
        RecommendationReceivedEvent event = buildRecommendationReceivedEvent(recommendation);
        recommendationReceivedEventPublisher.publish(event);
    }

    private RecommendationReceivedEvent buildRecommendationReceivedEvent(RecommendationDto recommendation) {
        return RecommendationReceivedEvent.builder()
                .authorId(recommendation.authorId())
                .receiverId(recommendation.receiverId())
                .recommendationId(recommendation.id())
                .build();
    }
}
