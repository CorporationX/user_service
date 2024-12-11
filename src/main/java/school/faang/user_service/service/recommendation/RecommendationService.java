package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.recommendation.ErrorMessage;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.publisher.recommendation.RecommendationEventPublisher;
import school.faang.user_service.publisher.recommendation.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationReceivedEventPublisher publisher;
    private RecommendationEventPublisher recommendationEventPublisher;


    @Transactional
    public ResponseRecommendationDto create(RequestRecommendationDto requestRecommendationDto) {
        log.info("Creating a recommendation from user with id {} for user with id {}",
                requestRecommendationDto.getAuthorId(), requestRecommendationDto.getReceiverId());

        recommendationValidator.validateRecommendation(requestRecommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(requestRecommendationDto);
        recommendation = processAndSaveRecommendation(recommendation);


        createRecommendationPublisher(requestRecommendationDto.getAuthorId()
                ,requestRecommendationDto.getReceiverId()
                ,recommendation.getId());

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public ResponseRecommendationDto createRecommendationAfterRequestAccepting(RecommendationRequest recommendationRequest) {
        log.info("Creating a recommendation from user with id {} for user with id {}",
                recommendationRequest.getReceiver().getId(), recommendationRequest.getRequester().getId());

        recommendationValidator.checkIfAcceptableTimeForRecommendation(recommendationRequest);

        Recommendation recommendation = recommendationMapper.fromRequestEntity(recommendationRequest);
        recommendation = processAndSaveRecommendation(recommendation);

        recommendationRequest.setRecommendation(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public ResponseRecommendationDto update(Long id, RequestRecommendationDto requestRecommendationDto) {
        log.info("Updating recommendation with id {}", id);

        recommendationValidator.validateRecommendation(requestRecommendationDto);
        Recommendation existingRecommendation = getRecommendation(id);
        recommendationMapper.updateFromDto(requestRecommendationDto, existingRecommendation);
        addGuarantees(existingRecommendation);

        existingRecommendation = recommendationRepository.save(existingRecommendation);
        log.info("Recommendation with id {} successfully updated", id);

        return recommendationMapper.toDto(existingRecommendation);
    }

    @Transactional
    public void delete(long id) {
        log.info("Deleting recommendation with id {}", id);
        recommendationRepository.deleteById(id);
        log.info("Recommendation with id {} successfully deleted", id);
    }

    public List<ResponseRecommendationDto> getAllUserRecommendations(long receiverId) {
        log.info("Getting all recommendations for user with id {}", receiverId);
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        log.debug("Found {} recommendations for user with id {}", recommendations.getTotalElements(), receiverId);
        return recommendationMapper.toDtoList(recommendations.getContent());
    }

    public List<ResponseRecommendationDto> getAllGivenRecommendations(long authorId) {
        log.info("Getting all recommendations created by user with id {}", authorId);
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        log.debug("User with id {} created {} recommendations", authorId, recommendations.getTotalElements());
        return recommendationMapper.toDtoList(recommendations.getContent());
    }

    private Recommendation getRecommendation(Long recommendationId) {
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> {
                    log.warn("Recommendation with id {} not found", recommendationId);
                    return new NoSuchElementException(
                            String.format("There is no recommendation with id = %d", recommendationId));
                });
    }

    private Skill getSkill(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    log.warn("Skill with id {} not found", skillId);
                    return new NoSuchElementException(
                            String.format("There is no skill with id = %d", skillId));
                });
    }

    private void addGuarantees(Recommendation recommendation) {
        List<SkillOffer> skillOfferList = Optional.ofNullable(recommendation.getSkillOffers())
                .orElseThrow(() -> {
                    log.warn("No skill offers to process for recommendation with id {}", recommendation.getId());
                    return new DataValidationException(ErrorMessage.NO_SKILL_OFFERS);
                });

        for (SkillOffer skillOffer : skillOfferList) {
            Skill skill = getSkill(skillOffer.getSkill().getId());

            skillRepository.findUserSkill(skillOffer.getSkill().getId(), recommendation.getReceiver().getId())
                    .ifPresent(existingSkill -> {
                        if (!isAuthorAlreadyGuarantor(recommendation, existingSkill)) {
                            addGuaranteeToSkill(recommendation, existingSkill);
                            log.debug("Added guarantee for skill '{}' to user '{}'",
                                    skill.getTitle(), recommendation.getReceiver().getId());
                        }
                    });
        }
    }

    private boolean isAuthorAlreadyGuarantor(Recommendation recommendation, Skill skill) {
        return skill.getGuarantees().stream()
                .map(UserSkillGuarantee::getGuarantor)
                .map(User::getId)
                .anyMatch(recommendation.getAuthor().getId()::equals);
    }

    private void addGuaranteeToSkill(Recommendation recommendation, Skill skill) {
        User receiver = recommendationValidator.validateUser(recommendation.getReceiver().getId());
        User author = recommendationValidator.validateUser(recommendation.getAuthor().getId());

        UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(author)
                .build();

        skill.getGuarantees().add(guarantee);
        skillRepository.save(skill);
    }

    private Recommendation processAndSaveRecommendation(Recommendation recommendation) {
        Recommendation finalRecommendation = recommendation;
        List<SkillOffer> skillOffers = recommendation.getSkillOffers().stream()
                .map(skillOfferDto -> {
                    Skill skill = getSkill(skillOfferDto.getSkill().getId());
                    return SkillOffer.builder()
                            .recommendation(finalRecommendation)
                            .skill(skill)
                            .build();
                }).toList();
        recommendation.setSkillOffers(skillOffers);

        recommendation = recommendationRepository.save(recommendation);
        log.info("Recommendation with id {} successfully created", recommendation.getId());

        addGuarantees(recommendation);

        return recommendation;
    }

    private void createRecommendationPublisher(long authorId, long receiverId, long recommendationId) {
        RecommendationReceivedEvent event = new RecommendationReceivedEvent(authorId, receiverId, recommendationId);
        publisher.publish(event);

        LocalDateTime timestamp = LocalDateTime.now();
        RecommendationEvent recommendationEvent = new RecommendationEvent(recommendationId, authorId, receiverId, timestamp);

        recommendationEventPublisher.publish(recommendationEvent);
        log.info("Both RecommendationReceivedEvent and RecommendationEvent published for recommendationId={}", recommendationId);

    }
}
