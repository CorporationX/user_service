package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
import school.faang.user_service.entity.mapper.RecommendationMapper;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    @Value("${recommendation.service.recommendation_period_in_month}")
    private int RECOMMENDATION_PERIOD_IN_MONTH;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        validateAll(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.save(recommendation);
        saveSkillOffers(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        validateRecommendationForUpdate(recommendationDto);
        validateAll(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        saveSkillOffers(recommendation);
        recommendationRepository.save(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional(readOnly = true)
    public void delete(Long id) {
        recommendationRepository.deleteById(id);
    }

    public Page<RecommendationDto> getAllUserRecommendation(long receiverId, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Recommendation> receiverRecommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable);

        return receiverRecommendations.map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllRecommendation(long authorId, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Recommendation> authorRecommendations = recommendationRepository.findAllByAuthorId(authorId, pageable);

        return authorRecommendations.map(recommendationMapper::toDto);
    }

    private void saveSkillOffers(Recommendation recommendation) {
        long authorId = recommendation.getAuthor().getId();
        long userId = recommendation.getReceiver().getId();
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        List<Skill> usersSkills = getUsersSkill(userId);

        for (SkillOffer skillOffer : skillOffers) {
            long skillId = skillOffer.getSkill().getId();
            if (usersSkills.contains(skillOffer.getSkill()) && guaranteeNotExist(userId, skillId, authorId)) {
                User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User not found."));
                Skill skill = skillRepository.findById(skillId).orElseThrow(() -> new DataValidationException("Skill not found."));
                User guarantUser = userRepository.findById(authorId).orElseThrow(() -> new DataValidationException("Guarantor not found."));

                UserSkillGuarantee guarantee = new UserSkillGuarantee();
                guarantee.setUser(user);
                guarantee.setSkill(skill);
                guarantee.setGuarantor(guarantUser);

                userSkillGuaranteeRepository.save(guarantee);
            } else {
                skillOfferRepository.save(skillOffer);
            }
        }
    }

    private List<Skill> getUsersSkill(long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getSkills)
                .orElse(Collections.emptyList());
    }

    private boolean guaranteeNotExist(long userId, long skillId, long guarantorId) {
        return !(userSkillGuaranteeRepository.existsById(userId) &&
                userSkillGuaranteeRepository.existsById(skillId) &&
                userSkillGuaranteeRepository.existsById(guarantorId));
    }

    private void validateAll(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOffersDto = recommendationDto.getSkillOffers();

        validationLastUpdate(recommendationDto);
        validateSkills(skillOffersDto);
        validateSkillInRepository(skillOffersDto);
    }

    private void validationLastUpdate(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long userId = recommendationDto.getReceiverId();

        Optional<Recommendation> lastRecommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, userId);

        if (lastRecommendation.isPresent()) {
            LocalDateTime lastUpdate = lastRecommendation.get().getUpdatedAt();
            LocalDateTime now = LocalDateTime.now();

            if (lastUpdate.plusMonths(RECOMMENDATION_PERIOD_IN_MONTH).isAfter(now)) {
                String errorMessage =
                        String.format("User with this ID %d cannot give recommendations yet, since %d months have not passed yet.",
                                userId, RECOMMENDATION_PERIOD_IN_MONTH);
                throw new DataValidationException(errorMessage);
            }
        }
    }

    private void validateSkills(List<SkillOfferDto> skillOfferDtos) {
        if (skillOfferDtos == null || skillOfferDtos.isEmpty()) {
            throw new DataValidationException("Skills null or empty.");
        }
    }

    private void validateSkillInRepository(List<SkillOfferDto> skills) {
        List<Long> skillIds = getUniqueSkillIds(skills);

        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                throw new DataValidationException("Skill " + skillId + " doesnt exist in system.");
            }
        }
    }

    private List<Long> getUniqueSkillIds(List<SkillOfferDto> skills) {
        return skills.stream()
                .map(SkillOfferDto::getSkillId)
                .distinct()
                .toList();
    }

    private void validateRecommendationForUpdate(RecommendationDto recommendationDto) {
        Long id = recommendationDto.getId();
        if (id == null) {
            throw new DataValidationException("Recommendation ID is null.");
        }
        recommendationRepository.findById(recommendationDto.getId())
                .orElseThrow(() -> new DataValidationException("Update is failed."));
    }
}