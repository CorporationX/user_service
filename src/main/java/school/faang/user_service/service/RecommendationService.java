package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.jpa.UserJpaRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserJpaRepository userJpaRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidator.validateAll(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendationRepository.save(recommendation);
        saveSkillOffers(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        recommendationValidator.validateRecommendationForUpdate(recommendationDto);
        recommendationValidator.validateAll(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        saveSkillOffers(recommendation);
        recommendationRepository.save(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    @Transactional
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
                User user = userJpaRepository.findById(userId).orElseThrow(() -> new DataValidationException("User not found."));
                Skill skill = skillRepository.findById(skillId).orElseThrow(() -> new DataValidationException("Skill not found."));
                User guarantUser = userJpaRepository.findById(authorId).orElseThrow(() -> new DataValidationException("Guarantor not found."));

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
        Optional<User> user = userJpaRepository.findById(userId);
        return user.map(User::getSkills)
                .orElse(Collections.emptyList());
    }

    private boolean guaranteeNotExist(long userId, long skillId, long guarantorId) {
        return !(userSkillGuaranteeRepository.existsById(userId) &&
                userSkillGuaranteeRepository.existsById(skillId) &&
                userSkillGuaranteeRepository.existsById(guarantorId));
    }
}