package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.RecommendationValidator;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationValidator recommendationValidator;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    public Long create(RecommendationDto recommendation) {
        recommendationValidator.recommendationTimeValidation(recommendation);
        recommendationValidator.skillEmptyValidation(recommendation);

        saveSkillOffer(recommendation);
        addGuaranteeUserSkill(recommendation);

        return recommendationRepository.create(recommendation.getAuthorId(),
                recommendation.getReceiverId(), recommendation.getContent());
    }

    public void update(RecommendationDto recommendation) {
        recommendationValidator.skillEmptyValidation(recommendation);

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        saveSkillOffer(recommendation);
        addGuaranteeUserSkill(recommendation);

        recommendationRepository.update(recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
    }

    public void delete(Long id) {
        recommendationRepository.deleteById(id);
    }

    public Page<RecommendationDto> getAllUserRecommendations(Long receiverId, int page, int pageSize) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, PageRequest.of(page, pageSize));
        return recommendations.map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllGivenRecommendations(Long authorId, int page, int pageSize) {
        findUserById(authorId);
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByAuthorId(authorId, PageRequest.of(page, pageSize));
        return recommendationPage.map(recommendationMapper::toDto);
    }

    private void saveSkillOffer(RecommendationDto recommendationDto) {
        recommendationDto.getSkillOffers()
                .forEach(skillOfferDto -> skillOfferRepository.create(skillOfferDto.getSkillId(), recommendationDto.getId()));
    }

    private void addGuaranteeUserSkill(RecommendationDto recommendationDto) {
        Set<Long> skillsIdSet = skillRepository
                .findAllByUserId(recommendationDto.getReceiverId())
                .stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> skillOfferDtoSet = recommendationDto.
                getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkillId)
                .collect(Collectors.toSet());

        skillsIdSet.retainAll(skillOfferDtoSet);
        skillsIdSet.forEach(skillId -> addUserSkillGuarantee(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                skillId));
    }

    private void addUserSkillGuarantee(Long authorId, Long receiverId, Long skillId) {
        User author = findUserById(authorId);
        User receiver = findUserById(receiverId);
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not found"));
        userSkillGuaranteeRepository.save(new UserSkillGuarantee(null, receiver, skill, author));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User with id: " + userId + " not found"));
    }
}
