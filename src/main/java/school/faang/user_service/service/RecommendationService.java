package school.faang.user_service.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.exception.recommendation.EntityException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static school.faang.user_service.exception.recommendation.RecommendationError.*;

@Service
@RequiredArgsConstructor
@Transactional
@Data
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final static int INTERVAL_DATE = 6;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        Long userId = recommendationDto.getAuthorId();
        Long receiver = recommendationDto.getReceiverId();

        validateInterval(userId, receiver);
        validateSkill(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        processSkillAndGuarantees(recommendation);
        recommendationRepository.save(recommendation);


        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        Long userId = recommendationDto.getAuthorId();
        Long receiver = recommendationDto.getReceiverId();

        validateInterval(userId, receiver);
        validateSkill(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        processSkillAndGuarantees(recommendation);
        recommendationRepository.save(recommendation);


        return recommendationMapper.toDto(recommendation);
    }

    public void delete(long id) {
        validateAvailabilityRecommendation(id);
        recommendationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllUserRecommendations(long receiverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recommendation> allRecommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable);

        return allRecommendations.getContent().stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllGivenRecommendations(long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recommendation> allRecommendations = recommendationRepository.findAllByAuthorId(authorId, pageable);

        return allRecommendations.getContent().stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    private void validateInterval(Long userId, Long receiverId) {
        User author = findUserById(userId);
        if (!userRepository.existsById(receiverId)) {
            throw new EntityException(ENTITY_IS_NOT_FOUND);
        }

        List<Recommendation> recommendationsGiven = author.getRecommendationsGiven();
        if (recommendationsGiven == null) {
            recommendationsGiven = new ArrayList<>();
        }

        Optional<LocalDateTime> lastRecommendationDate = recommendationsGiven.stream()
                .filter(recommendation -> recommendation.getReceiver().getId() == receiverId)
                .map(Recommendation::getCreatedAt)
                .max(LocalDateTime::compareTo);

        lastRecommendationDate.ifPresent(lastDate -> {
            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime minimumIntervalDate = currentDate.minusMonths(INTERVAL_DATE);

            if (lastDate.isAfter(minimumIntervalDate)) {
                throw new DataValidationException(RECOMMENDATION_EXPIRATION_TIME_NOT_PASSED);
            }
        });
    }

    private void validateSkill(RecommendationDto recommendationDto) {
        List<Long> skillOffersDto = recommendationDto.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        List<SkillOffer> skillOffers = (List<SkillOffer>) skillOfferRepository.findAllById(skillOffersDto);

        Set<Long> existingSkillIds = skillOffers.stream()
                .map(skill -> skill.getSkill().getId())
                .collect(Collectors.toSet());

        boolean skillInRepository = existingSkillIds.containsAll(skillOffersDto);

        if (!skillInRepository) {
            throw new DataValidationException(SKILL_IS_NOT_FOUND);
        }
    }

    private void validateAvailabilityRecommendation(long id) {
        if (recommendationRepository.findById(id).isEmpty()) {
            throw new DataValidationException(RECOMMENDATION_IS_NOT_FOUND);
        }
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityException(ENTITY_IS_NOT_FOUND));
    }

    private void processSkillAndGuarantees(Recommendation recommendation) {
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();

        long authorId = author.getId();

        List<Skill> existingReceiverSkills = skillRepository.findAllByUserId(receiver.getId());
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();

        boolean authorHasGuarantee = userSkillGuaranteeRepository.existsById(authorId);

        skillOffers.forEach(skillOffer -> {
            long skillOfferId = skillOffer.getId();
            if (existingReceiverSkills.contains(skillOffer.getSkill())
                    && !authorHasGuarantee) {
                addNewGuarantee(author, receiver, skillOfferId);
            } else {
                skillOfferRepository.save(skillOffer);
            }
        });
    }

    private void addNewGuarantee(User guarantee, User receiver, Long skillId) {
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