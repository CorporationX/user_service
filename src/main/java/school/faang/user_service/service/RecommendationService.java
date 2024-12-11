package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.util.CollectionUtils;
import school.faang.user_service.util.SkillUtils;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private static final long REQUIRED_DURATION_IN_DAYS = 6 * 30;
    public static final String THE_REQUIRED_PERIOD_HAS_NOT_PASSED = "Less than 6 months since last referral";

    private final RecommendationRepository recommendationRepo;
    private final UserService userService;
    private final SkillService skillService;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferMapper skillOfferMapper;
    private final RecommendationValidator recommendationValidator;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        log.info("Creating new recommendation for receiverId: {}", recommendationDto.receiverId());
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        establishRelations(recommendation, recommendationDto);
        validateRecommendation(recommendation);
        processSkillDependencies(recommendation);
        recommendation = recommendationRepo.save(recommendation);
        log.info("Recommendation created successfully with id: {}", recommendation.getId());
        return recommendationMapper.toDto(recommendation);
    }

    private void processSkillDependencies(Recommendation recommendation) {
        List<Skill> recommendationSkills = SkillUtils.toSkillList(recommendation.getSkillOffers());
        addSkillToReceiverIfAbsent(recommendation, recommendationSkills);
        addGuaranteeToReceiverSkillIfAbsent(recommendation, recommendationSkills);
    }

    private void establishRelations(Recommendation recommendation, RecommendationDto recommendationDto) {
        User author = userService.getUserById(recommendationDto.authorId());
        User receiver = userService.getUserById(recommendationDto.receiverId());
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setSkillOffers(buildSkillOffers(recommendation, recommendationDto));
    }

    @Transactional
    public void delete(long recommendationId) {
        log.info("Deleting recommendation with id: {}", recommendationId);
        Recommendation recommendation = recommendationRepo.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", "id", recommendationId));
        List<Skill> offeredSkills = SkillUtils.toSkillList(recommendation.getSkillOffers());

        removeGuaranteeIfNoOtherRecommendations(recommendation, offeredSkills);
        removeUserSkillIfNoOtherGuarantees(recommendation, offeredSkills);
        unlinkUsersFromRecommendation(recommendation);
        recommendationRepo.delete(recommendation);
        log.info("Recommendation with id: {} deleted successfully", recommendationId);
    }

    private void unlinkUsersFromRecommendation(Recommendation recommendation) {
        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();
        receiver.removeReceivedRecommendation(recommendation);
        author.removeGivenRecommendation(recommendation);
    }

    @Transactional
    public RecommendationDto update(long recommendationId, RecommendationDto recommendationDto) {
        log.info("Updating recommendation with id: {}", recommendationId);
        Recommendation recommendation = recommendationRepo.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", "id", recommendationId));
        validateRecommendation(recommendation);

        SkillChanges skillChanges = identifySkillChanges(recommendation, recommendationDto);

        recommendation.setContent(recommendationDto.content());
        recommendation.updateSkillOffers(buildSkillOffers(recommendation, recommendationDto));

        processSkillChanges(recommendation, skillChanges);
        Recommendation updatedRecommendation = recommendationRepo.save(recommendation);
        log.info("Recommendation with id: {} updated successfully", recommendationId);
        return recommendationMapper.toDto(updatedRecommendation);
    }

    private record SkillChanges(List<Skill> removedSkills, List<Skill> newSkills) {
    }

    private SkillChanges identifySkillChanges(Recommendation recommendation, RecommendationDto recommendationDto) {
        List<Skill> oldSkills = SkillUtils.toSkillList(recommendation.getSkillOffers());
        List<Skill> updatedSkills = skillService.getSkillsFrom(recommendationDto.skillOffers());

        return new SkillChanges(
                CollectionUtils.findMissingElements(oldSkills, updatedSkills),
                CollectionUtils.findMissingElements(updatedSkills, oldSkills)
        );
    }

    private void processSkillChanges(Recommendation recommendation, SkillChanges skillChanges) {
        removeGuaranteeIfNoOtherRecommendations(recommendation, skillChanges.removedSkills());
        removeUserSkillIfNoOtherGuarantees(recommendation, skillChanges.removedSkills());
        addSkillToReceiverIfAbsent(recommendation, skillChanges.newSkills());
        addGuaranteeToReceiverSkillIfAbsent(recommendation, skillChanges.newSkills());
    }

    private void removeUserSkillIfNoOtherGuarantees(Recommendation recommendation, List<Skill> removedSkills) {
        User receiver = recommendation.getReceiver();
        List<Recommendation> otherReceiverRecommendations = CollectionUtils.excludeItemFrom(
                recommendationRepo.findAllByReceiverId(receiver.getId()),
                recommendation);
        Set<Skill> skillsOtherReceiverRecommendations = new HashSet<>(
                SkillUtils.getSkillsFromRecommendations(otherReceiverRecommendations));

        removedSkills.stream()
                .filter(skill -> !skillsOtherReceiverRecommendations.contains(skill))
                .forEach(skill -> {
                    receiver.removeSkill(skill);
                    skill.removeUser(receiver);
                });
    }

    private void removeGuaranteeIfNoOtherRecommendations(Recommendation recommendation, List<Skill> guaranteedSkills) {
        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();
        List<Recommendation> otherRecommendations = CollectionUtils.excludeItemFrom(
                recommendationRepo.findAllByReceiverIdAndAuthorId(receiver.getId(), author.getId()),
                recommendation);
        Set<Skill> skillsOfOtherRecommends = new HashSet<>(
                SkillUtils.getSkillsFromRecommendations(otherRecommendations));

        guaranteedSkills.stream()
                .filter(skill -> !skillsOfOtherRecommends.contains(skill))
                .forEach(skill -> skill.removeSameGuarantee(new UserSkillGuarantee(receiver, skill, author)));
    }

    private static void addSkillToReceiverIfAbsent(Recommendation recommendation, List<Skill> addedSkills) {
        User receiver = recommendation.getReceiver();
        Set<Skill> receiverSkills = new HashSet<>(receiver.getSkills());
        log.debug("Adding skills to receiver if absent, receiverId: {}", receiver.getId());

        addedSkills.stream()
                .filter(skill -> !receiverSkills.contains(skill))
                .forEach(skill -> {
                    receiver.addSkill(skill);
                    skill.addUser(receiver);
                    log.info("Added skill '{}' to receiver '{}'", skill.getTitle(), receiver.getId());
                });
    }

    private void addGuaranteeToReceiverSkillIfAbsent(Recommendation recommendation, List<Skill> addedSkills) {
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();

        receiver.getSkills().stream()
                .filter(addedSkills::contains)
                .filter(skill -> !skill.userIsGuarantor(author))
                .forEach(skill -> {
                    UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                            .user(receiver)
                            .skill(skill)
                            .guarantor(author)
                            .build();
                    skill.addGuarantee(guarantee);
                });
    }

    public Page<RecommendationDto> getAllUserRecommendations(long receiverId, Pageable pageable) {
        Page<Recommendation> recommendationsReceived = recommendationRepo.findAllByReceiverId(receiverId, pageable);
        return recommendationsReceived.map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllRecommendations(Pageable pageable) {
        Page<Recommendation> recommendations = recommendationRepo.findAll(pageable);
        return recommendations.map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
        Page<Recommendation> recommendationsGiven = recommendationRepo.findAllByAuthorId(authorId, pageable);
        return recommendationsGiven.map(recommendationMapper::toDto);
    }

    private void validateRecommendation(Recommendation newRecommendation) {
        log.debug("Validating recommendation for receiver: {}, author: {}", newRecommendation.getReceiver().getId(), newRecommendation.getAuthor().getId());
        if (!recommendationValidator.isPeriodElapsedSinceLastRecommendation(
                getLastReceiverRecommendationFromAuthor(newRecommendation),
                newRecommendation,
                Duration.ofDays(REQUIRED_DURATION_IN_DAYS))) {
            log.warn("Validation failed: Less than 6 months since last referral for receiver: {}",
                    newRecommendation.getReceiver().getId());
            throw new DataValidationException(THE_REQUIRED_PERIOD_HAS_NOT_PASSED);
        }
    }

    private Recommendation getLastReceiverRecommendationFromAuthor(Recommendation recommendation) {
        User receiver = recommendation.getReceiver();
        User author = recommendation.getAuthor();
        return recommendationRepo
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(author.getId(), receiver.getId())
                .orElse(null);
    }

    private List<SkillOffer> buildSkillOffers(Recommendation recommendation, RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtos = recommendationDto.skillOffers();
        return skillOfferDtos.stream()
                .map(skillOfferDto -> {
                    SkillOffer skillOffer = skillOfferMapper.toEntity(skillOfferDto);
                    skillOffer.setSkill(skillService.getSkillById(skillOfferDto.skillId()));
                    skillOffer.setRecommendation(recommendation);
                    return skillOffer;
                }).toList();
    }
}
