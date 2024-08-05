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
import java.time.format.DateTimeFormatter;
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
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        public RecommendationDto create(RecommendationDto recommendationDto) {
            Long userId = recommendationDto.getAuthorId();
            Long receiver = recommendationDto.getReceiverId();

            validateInterval(userId, receiver);
            validateSkill(recommendationDto.getSkillOffers());

            Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

            processSkillAndGuarantees(recommendation);
            recommendationRepository.save(recommendation);


            return recommendationMapper.toDto(recommendation);
        }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        Long userId = recommendationDto.getAuthorId();
        Long receiver = recommendationDto.getReceiverId();

        validateInterval(userId, receiver);
        validateSkill(recommendationDto.getSkillOffers());

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
            throw new EntityException(ENTITY_IS_NOT_FOUND, receiverId);
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
            LocalDateTime minimumIntervalDate = lastDate.plusMonths(INTERVAL_DATE);

            if (currentDate.isBefore(minimumIntervalDate)) {
                throw new DataValidationException(
                        RECOMMENDATION_EXPIRATION_TIME_NOT_PASSED,
                        String.format("Try again after %s", minimumIntervalDate.format(formatter))
                );
            }
        });
    }

    private void validateSkill(List<SkillOfferDto> skillOffersFromDto) {
        Set<Long> skillOffersDto = skillOffersFromDto.stream()
                .map(SkillOfferDto::getSkillId)
                .collect(Collectors.toSet());
        List<SkillOffer> skillOffers = skillOfferRepository.findAllById(skillOffersDto);

        Set<Long> existingSkillIds = skillOffers.stream()
                .map(skill -> skill.getSkill().getId())
                .collect(Collectors.toSet());

        skillOffersDto.removeAll(existingSkillIds);

        if (!skillOffersDto.isEmpty()) {
            throw new DataValidationException(SKILL_IS_NOT_FOUND, skillOffersDto.toString());
        }
    }

    private void validateAvailabilityRecommendation(long id) {
        if (recommendationRepository.findById(id).isEmpty()) {
            throw new DataValidationException(RECOMMENDATION_IS_NOT_FOUND, Long.toString(id));
        }
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityException(ENTITY_IS_NOT_FOUND, id));
    }

    private void processSkillAndGuarantees(Recommendation recommendation) {
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();

        long authorId = author.getId();

        List<Skill> existingReceiverSkills = skillRepository.findAllByUserId(receiver.getId());
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();

        boolean authorHasGuarantee = userSkillGuaranteeRepository.existsById(authorId);

        List<Long> skillOfferIdsForGuarantees = new ArrayList<>();
        List<SkillOffer> skillOffersToSave = new ArrayList<>();

        skillOffers.forEach(skillOffer -> {
            long skillOfferId = skillOffer.getId();
            if (existingReceiverSkills.contains(skillOffer.getSkill())
                    && !authorHasGuarantee) {
                skillOfferIdsForGuarantees.add(skillOfferId);
            } else {
                skillOffersToSave.add(skillOffer);
            }
        });

        if (!skillOfferIdsForGuarantees.isEmpty()) {
            addNewGuarantee(author, receiver, skillOfferIdsForGuarantees);
        }

        if (!skillOffersToSave.isEmpty()) {
            skillOfferRepository.saveAll(skillOffersToSave);
        }

    }

    private void addNewGuarantee(User guarantor, User receiver, List<Long> skillOfferIds) {
        List<Skill> skills = skillRepository.findAllById(skillOfferIds);
        List<UserSkillGuarantee> guarantees = new ArrayList<>();

        Set<Long> foundSkillIds = skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> missingSkillIds = new HashSet<>(skillOfferIds);
        missingSkillIds.removeAll(foundSkillIds);

        if (!missingSkillIds.isEmpty())
            throw new DataValidationException(SKILL_IS_NOT_FOUND, missingSkillIds.toString());

        skills.forEach(skill -> {
                    UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                            .guarantor(guarantor)
                            .user(receiver)
                            .skill(skill)
                            .build();

                    guarantees.add(guarantee);
                }
        );

        userSkillGuaranteeRepository.saveAll(guarantees);
    }
}