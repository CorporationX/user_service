package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static school.faang.user_service.exception.recommendation.RecommendationError.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final static int INTERVAL_DATE = 6;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        validationIntervalAndSkill(recommendationDto);
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        processSkillAndGuarantees(recommendation);
        recommendationRepository.save(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        validationIntervalAndSkill(recommendationDto);
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        processSkillAndGuarantees(recommendation);

        recommendationRepository.save(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    public void delete(long id) {
        validationAvailabilityRecommendation(id);
        recommendationRepository.deleteById(id);
    }

    @Transactional
    public List<RecommendationDto> getAllUserRecommendations(long receiverId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Recommendation> allRecommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable);

        return allRecommendations.getContent().stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    @Transactional
    public List<RecommendationDto> getAllGivenRecommendations(long authorId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Recommendation> allRecommendations = recommendationRepository.findAllByAuthorId(authorId, pageable);

        return allRecommendations.getContent().stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    private void validationIntervalAndSkill(RecommendationDto recommendationDto) {
        User author = findUserById(recommendationDto.getAuthorId());
        User receiver = findUserById(recommendationDto.getReceiverId());

        List<Recommendation> recommendationsGiven = author.getRecommendationsGiven();
        if (recommendationsGiven == null) {
            recommendationsGiven = new ArrayList<>();
        }

        Optional<LocalDateTime> lastRecommendationDate = recommendationsGiven.stream()
                .filter(recommendation -> recommendation.getReceiver().getId() == receiver.getId())
                .map(Recommendation::getCreatedAt)
                .max(LocalDateTime::compareTo);

        lastRecommendationDate.ifPresent(lastDate -> {
            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime minimumIntervalDate = currentDate.minusMonths(INTERVAL_DATE);

            if (lastDate.isAfter(minimumIntervalDate)) {
                throw new DataValidationException(RECOMMENDATION_EXPIRATION_TIME_NOT_PASSED);
            }
        });

        boolean skillInRepository = recommendationDto.getSkillOffers().stream()
                .allMatch(skill -> skillOfferRepository.existsById(skill.getSkillId()));

        if (!skillInRepository) {
            throw new DataValidationException(SKILL_IS_NOT_FOUND);
        }
    }

    private void validationAvailabilityRecommendation(long id) {
        if (recommendationRepository.findById(id).isEmpty()) {
            throw new DataValidationException(RECOMMENDATION_IS_NOT_FOUND);
        }
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityException(ENTITY_IS_NOT_FOUND));
    }

    @Transactional
    public void processSkillAndGuarantees(Recommendation recommendation) {
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();

        long authorId = author.getId();

        List<Skill> existingReceiverSkills = skillRepository.findAllByUserId(receiver.getId());
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();

        skillOffers.forEach(skillOffer -> {
            long skillOfferId = skillOffer.getId();
            if (existingReceiverSkills.contains(skillOffer.getSkill())
                    && !userSkillGuaranteeRepository.existsById(authorId)) {
                addNewGuarantee(author, receiver, skillOfferId);
            } else {
                skillOfferRepository.save(skillOffer);
            }
        });
    }

    public void addNewGuarantee(User guarantee, User receiver, Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException(SKILL_IS_NOT_FOUND));

        UserSkillGuarantee guarantees = UserSkillGuarantee.builder()
                .guarantor(guarantee)
                .user(receiver)
                .skill(skill)
                .build();

        userSkillGuaranteeRepository.save(guarantees);
    }
}