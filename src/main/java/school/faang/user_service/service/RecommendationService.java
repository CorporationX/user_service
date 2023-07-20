package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public void create(RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);
        saveSkillOffer(recommendationDto);
        recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
    }

    public void saveSkillOffer(RecommendationDto recommendationDto) {
        checkAndAddSkillsGuarantor(recommendationDto);
        List<Long> skillOffersId = recommendationDto.getSkillOffers().stream().map(SkillOfferDto::getId).toList();
        List<SkillOffer> skillOffersEntity = skillOfferRepository.findAllById(skillOffersId);
        skillOfferRepository.saveAll(skillOffersEntity);
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        Recommendation updatedRecommendation = recommendationRepository.update(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        saveSkillOffer(recommendation);
        return recommendationMapper.toDto(updatedRecommendation);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(recieverId, Pageable.unpaged());
        if (recommendations.isEmpty()) {
            throw new DataValidationException("No recommendations found for the user with ID: " + recieverId);
        }
        return recommendationMapper.toRecommendationDtos(recommendations.getContent());
    }

    public void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getAuthorId() == null) {
            throw new DataValidationException("Author ID must be specified");
        }
        if (recommendationDto.getReceiverId() == null) {
            throw new DataValidationException("Receiver ID must be specified");
        }
        if (recommendationDto.getSkillOffers() != null) {
            Optional<Recommendation> recommendation = recommendationRepository.
                    findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
            if (recommendation.isPresent()) {
                LocalDateTime recommendationCreateTime = recommendation.get().getCreatedAt();
                boolean allSkillsExistInSystem = recommendationDto.getSkillOffers().stream()
                        .allMatch(skill -> skillRepository.existsById(skill.getId()));
                if (recommendationCreateTime.isAfter(LocalDateTime.now().minusMonths(6))) {
                    throw new DataValidationException("Recommendation must be given less then 6 months");
                } else if (!allSkillsExistInSystem) {
                    throw new DataValidationException("Some of the skills do not exist in our system");
                }
            }
        }
    }

    public void checkAndAddSkillsGuarantor(RecommendationDto recommendationDto) {
        List<Skill> userSkills = skillRepository.findAllByUserId(recommendationDto.getReceiverId());
        List<Skill> offeredSkills = getOfferedSkillsFromDatabase(recommendationDto);
        userSkills.stream()
                .flatMap(userSkill -> offeredSkills.stream()
                        .filter(offeredSkill -> userSkill.getTitle().equals(offeredSkill.getTitle())))
                .forEach(sameSkill -> {
                    if (isAuthorGuarantor(sameSkill, recommendationDto.getAuthorId())) {
                        User currentUser = userRepository.findById(recommendationDto.getReceiverId()).orElseThrow(() -> new DataValidationException("Entity not found"));
                        User guarantor = userRepository.findById(recommendationDto.getAuthorId()).orElseThrow(() -> new DataValidationException("Entity not found"));
                        UserSkillGuarantee newUserSkillGuarantee = UserSkillGuarantee.builder().user(currentUser).skill(sameSkill).guarantor(guarantor).build();
                        sameSkill.getGuarantees().add(newUserSkillGuarantee);
                    }
                    recommendationDto.getSkillOffers().removeIf(skillOfferDto -> skillOfferDto.getSkillId() == sameSkill.getId());
                });
        User currentUser = userRepository.findById(recommendationDto.getReceiverId()).orElseThrow(() -> new DataValidationException("Entity not found"));
        currentUser.setSkills(userSkills);
        userRepository.save(currentUser);
    }

    public List<Skill> getOfferedSkillsFromDatabase(RecommendationDto recommendation) {
        List<Long> skillsId = recommendation.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        return skillRepository.findAllById(skillsId);
    }

    private boolean isAuthorGuarantor(Skill userSkill, long authorId) {
        return userSkill.getGuarantees().stream()
                .map(UserSkillGuarantee::getGuarantor)
                .map(User::getId)
                .noneMatch(guaranteeId -> guaranteeId.equals(authorId));
    }
}