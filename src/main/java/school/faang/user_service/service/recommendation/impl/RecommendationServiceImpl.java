package school.faang.user_service.service.recommendation.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.dto.event.RecommendationReceivedEvent;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationReceivedEventPublisher eventPublisher;

    @Override
    @Transactional
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        log.info("Creating a recommendation from user with id {} for user with id {}",
                recommendationDto.authorId(), recommendationDto.receiverId());

        User author = userRepository.findById(recommendationDto.authorId())
                .orElseThrow(() -> {
                    log.error("Author with id {} not found", recommendationDto.authorId());
                    return new DataValidationException(
                            "Author with id " + recommendationDto.authorId() + " not found");
                });

        User receiver = userRepository.findById(recommendationDto.receiverId())
                .orElseThrow(() -> {
                    log.error("Receiver with id {} not found", recommendationDto.receiverId());
                    return new DataValidationException(
                            "Receiver with id " + recommendationDto.receiverId() + " not found");
                });

        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setCreatedAt(LocalDateTime.now());

        recommendationRepository.save(recommendation);
        log.info("Recommendation with id {} successfully saved", recommendation.getId());

        handleSkillOffers(recommendation, recommendationDto.skillOffers());

        publishRecommendationReceivedEvent(recommendation);

        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    public List<RecommendationDto> getAllUserRecommendations(long receiverId, Pageable pageable) {
        log.info("Getting all recommendations for user with id {}", receiverId);

        List<RecommendationDto> recommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable)
                .stream()
                .map(recommendationMapper::toRecommendationDto)
                .toList();

        log.debug("Found {} recommendations for user with id {}", recommendations.size(), receiverId);
        return recommendations;
    }

    @Override
    public List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
        log.info("Getting all recommendations created by user with id {}", authorId);

        List<RecommendationDto> recommendations = recommendationRepository.findAllByAuthorId(authorId, pageable)
                .stream()
                .map(recommendationMapper::toRecommendationDto)
                .toList();

        log.debug("User with id {} created {} recommendations", authorId, recommendations.size());
        return recommendations;
    }

    @Override
    @Transactional
    public RecommendationDto updateRecommendation(long id, RecommendationDto recommendationDto) {
        log.info("Updating recommendation with id {}", id);

        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Recommendation with id {} not found", id);
                    return new DataValidationException("Recommendation with id " + id + " not found");
                });

        recommendationMapper.updateFromDto(recommendationDto, recommendation);
        recommendation.setUpdatedAt(LocalDateTime.now());

        recommendationRepository.save(recommendation);
        log.info("Recommendation with id {} successfully updated", id);

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        log.info("Skill offers for recommendation with id {} deleted", id);

        handleSkillOffers(recommendation, recommendationDto.skillOffers());

        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    @Transactional
    public void deleteRecommendation(long id) {
        log.info("Deleting recommendation with id {}", id);

        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Recommendation with id {} not found", id);
                    return new DataValidationException("Recommendation with id " + id + " not found");
                });

        recommendationRepository.delete(recommendation);
        log.info("Recommendation with id {} successfully deleted", id);
    }

    private void handleSkillOffers(Recommendation recommendation, List<SkillOfferDto> skillOffers) {
        log.info("Handling skill offers for recommendation with id {}", recommendation.getId());

        List<Long> skillIds = skillOffers.stream()
                .map(SkillOfferDto::skillId)
                .toList();

        List<Skill> skills = skillRepository.findAllById(skillIds);

        Map<Long, Skill> skillsMap = skills.stream()
                .collect(Collectors.toMap(Skill::getId, skill -> skill));

        List<SkillOffer> skillOffersToSave = skillOffers.stream().map(skillOfferDto -> {
            Skill skill = skillsMap.get(skillOfferDto.skillId());
            if (skill == null) {
                log.error("Skill with id {} not found", skillOfferDto.skillId());

                throw new DataValidationException("Skill with id " + skillOfferDto.skillId() + " not found");
            }

            return SkillOffer.builder()
                    .skill(skill)
                    .recommendation(recommendation)
                    .build();
        }).toList();

        skillOfferRepository.saveAll(skillOffersToSave);
        log.info("Skill offers for recommendation with id {} successfully saved", recommendation.getId());
    }

    private void publishRecommendationReceivedEvent(Recommendation recommendation) {
        RecommendationReceivedEvent event = new RecommendationReceivedEvent(
                recommendation.getId(),
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId()
        );
        eventPublisher.publish(event);
        log.info("RecommendationReceivedEvent event published: {}", event);
    }
}