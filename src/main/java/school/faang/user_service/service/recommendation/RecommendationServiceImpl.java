package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.RecommendationFilterStrategy;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    @Value("${recommendation.cooldown.months:6}")
    private int recommendationCooldownMonths;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserContext userContext;
    private final List<RecommendationFilterStrategy> recommendationFilters;

    @Override
    @Transactional
    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        long authorId = userContext.getUserId();
        log.info("Creating recommendation from user {} to user {}", authorId, recommendationDto.receiverId());

        if (authorId == recommendationDto.receiverId()) {
            throw new DataValidationException("User cannot write recommendation to themselves");
        }

        User receiver = userRepository.getByIdOrThrow(recommendationDto.receiverId());
        User author = userRepository.getByIdOrThrow(authorId);

        checkRecommendationCooldown(authorId, recommendationDto.receiverId());

        Recommendation recommendation = Recommendation.builder()
                .author(author)
                .receiver(receiver)
                .content(recommendationDto.content())
                .build();
        
        recommendation = recommendationRepository.save(recommendation);

        if (recommendationDto.skillIds() != null && !recommendationDto.skillIds().isEmpty()) {
            processSkillOffers(recommendation, recommendationDto.skillIds(), receiver, author);
        }

        log.info("Recommendation {} created successfully", recommendation.getId());
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    @Transactional
    public RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        long authorId = userContext.getUserId();
        log.info("Updating recommendation {} by user {}", recommendationId, authorId);

        if (StringUtils.isBlank(recommendationDto.content())) {
            throw new DataValidationException("Content cannot be empty");
        }

        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation not found"));

        if (recommendation.getAuthor().getId() != authorId) {
            throw new ForbiddenException("Only recommendation author can update it");
        }

        recommendation.setContent(recommendationDto.content());

        if (recommendationDto.skillIds() != null) {

            skillOfferRepository.deleteAllByRecommendationId(recommendationId);

            if (!recommendationDto.skillIds().isEmpty()) {
                processSkillOffers(recommendation, recommendationDto.skillIds(), 
                        recommendation.getReceiver(), recommendation.getAuthor());
            }
        }

        recommendation = recommendationRepository.save(recommendation);
        log.info("Recommendation {} updated successfully", recommendationId);
        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    @Transactional
    public void delete(long recommendationId) {
        long authorId = userContext.getUserId();
        log.info("Deleting recommendation {} by user {}", recommendationId, authorId);

        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation not found"));

        if (recommendation.getAuthor().getId() != authorId) {
            throw new ForbiddenException("Only recommendation author can delete it");
        }

        skillOfferRepository.deleteAllByRecommendationId(recommendationId);

        int deletedCount = recommendationRepository.deleteByIdAndAuthor_id(recommendationId, authorId);
        if (deletedCount == 0) {
            throw new EntityNotFoundException("Recommendation not found or access denied");
        }

        log.info("Recommendation {} deleted successfully", recommendationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        log.info("Getting recommendations with filters: {}", filters);

        List<Recommendation> allRecommendations = recommendationRepository.findAll();
        
        return allRecommendations.stream()
                .filter(recommendation -> matchesFilters(recommendation, filters))
                .map(recommendationMapper::toRecommendationDto)
                .toList();
    }

    private void checkRecommendationCooldown(long authorId, long receiverId) {
        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        if (lastRecommendation.isPresent()) {
            LocalDateTime lastRecommendationTime = lastRecommendation.get().getCreatedAt();
            LocalDateTime cooldownEnd = lastRecommendationTime.plusMonths(recommendationCooldownMonths);
            
            if (LocalDateTime.now().isBefore(cooldownEnd)) {
                throw new DataValidationException(
                        String.format(
                                "Cannot create recommendation. Last recommendation was created less than %d months ago",
                                recommendationCooldownMonths)
                );
            }
        }
    }

    private void processSkillOffers(Recommendation recommendation, List<Long> skillIds, 
                                  User receiver, User author) {
        for (Long skillId : skillIds) {
            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new EntityNotFoundException("Skill not found: " + skillId));

            SkillOffer skillOffer = SkillOffer.builder()
                    .skill(skill)
                    .recommendation(recommendation)
                    .build();
            
            skillOffer = skillOfferRepository.save(skillOffer);
            addSkillOfferToRecommendation(recommendation, skillOffer);

            boolean userHasSkill = receiver.getSkills().stream()
                    .anyMatch(userSkill -> userSkill.getId().equals(skillId));

            if (userHasSkill) {
                addGuaranteeIfNotExists(receiver, skill, author);
            }
        }
    }

    private void addGuaranteeIfNotExists(User user, Skill skill, User guarantor) {
        long guaranteeCount = userSkillGuaranteeRepository.countByUserAndSkillAndGuarantor(
                user.getId(), skill.getId(), guarantor.getId()
        );

        if (guaranteeCount == 0) {
            UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                    .user(user)
                    .skill(skill)
                    .guarantor(guarantor)
                    .build();
            userSkillGuaranteeRepository.save(guarantee);
            log.info("Added guarantee for skill {} by user {} to user {}", 
                    skill.getId(), guarantor.getId(), user.getId());
        }
    }

    private void addSkillOfferToRecommendation(Recommendation recommendation, SkillOffer skillOffer) {
        if (recommendation.getSkillOffers() == null) {
            recommendation.setSkillOffers(new ArrayList<>());
        }

        recommendation.getSkillOffers().add(skillOffer);
        skillOffer.setRecommendation(recommendation);
    }

    private boolean matchesFilters(Recommendation recommendation, RecommendationFilterDto filters) {
        if (recommendationFilters == null || recommendationFilters.isEmpty()) {
            return true; // если фильтров нет, все рекомендации проходят
        }
        return recommendationFilters.stream()
                .filter(strategy -> strategy.isApplicable(filters))
                .allMatch(strategy -> strategy.matchesFilters(recommendation, filters));
    }
}
