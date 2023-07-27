package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
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
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {

        validatePreviousRecommendation(recommendationDto);

        recommendationDto.getSkillOffers()
                .forEach(skillOffer -> {
                    if (!skillRepository.existsById(skillOffer.getSkillId())) {
                        throw new DataValidationException(
                                String.format("Skill with id=%d is missing in db!", skillOffer.getSkillId()));
                    }
                });

        List<Long> skillIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        var skills = skillRepository.findAllById(skillIds);
        if (skillIds.size() != skills.size()) {
            throw new DataValidationException("Some skills do not exist");
        }

        List<Long> recommendationIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getRecommendationId)
                .toList();
        var recommendations = recommendationRepository.findAllById(recommendationIds);
        if (recommendationIds.size() != recommendations.size()) {
            throw new DataValidationException("Some recommendations do not exist");
        }

        recommendationDto.getSkillOffers()
                .forEach(sod -> skillOfferRepository.create(sod.getSkillId(), sod.getRecommendationId()));

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        User receiver = getUser(recommendationDto.getReceiverId());
        User author = getUser(recommendationDto.getAuthorId());

        recommendation.setReceiver(receiver);
        recommendation.setAuthor(author);

        recommendationDto.getSkillOffers() //10th point from ticket BC-3502
                .forEach(skillOffer -> {
                    Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillOffer.getSkillId(), receiver.getId());
                    if (optionalSkill.isPresent()) {
                        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                                .skill(optionalSkill.get())
                                .user(receiver)
                                .guarantor(author)
                                .build();
                        userSkillGuaranteeRepository.save(userSkillGuarantee);
                    }
                });

        recommendationRepository.save(recommendation); //12th point from ticket BC-3502
        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto){
        validatePreviousRecommendation(recommendationDto);

        recommendationDto.getSkillOffers()
                .forEach(skillOffer -> {
                    if (!skillRepository.existsById(skillOffer.getSkillId())) {
                        throw new DataValidationException(
                                String.format("Skill with id=%d is missing in db!", skillOffer.getSkillId()));
                    }
                });

        List<Long> skillIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        var skills = skillRepository.findAllById(skillIds);
        if (skillIds.size() != skills.size()) {
            throw new DataValidationException("Some skills do not exist");
        }

        List<Long> recommendationIds = recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getRecommendationId)
                .toList();
        var recommendations = recommendationRepository.findAllById(recommendationIds);
        if (recommendationIds.size() != recommendations.size()) {
            throw new DataValidationException("Some recommendations do not exist");
        }

        Recommendation updatedRecommendation = recommendationRepository
                .update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());

        recommendationDto.getSkillOffers()
                .forEach(sod -> skillOfferRepository.deleteAllByRecommendationId(sod.getRecommendationId()));

        recommendationDto.getSkillOffers()
                .forEach(sod -> skillOfferRepository.create(sod.getSkillId(), sod.getRecommendationId()));

        User receiver = getUser(recommendationDto.getReceiverId());
        User author = getUser(recommendationDto.getAuthorId());

        recommendationDto.getSkillOffers()
                .forEach(skillOffer -> {
                    Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillOffer.getSkillId(), receiver.getId());
                    if (optionalSkill.isPresent()) {
                        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                                .skill(optionalSkill.get())
                                .user(receiver)
                                .guarantor(author)
                                .build();
                        userSkillGuaranteeRepository.save(userSkillGuarantee);
                    }
                });

        return recommendationMapper.toDto(updatedRecommendation);
    }

    public void delete(long id){
        recommendationRepository.deleteById(id);
    }

    private void validatePreviousRecommendation(RecommendationDto recommendationDto) {
        var recommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        if (recommendation.isEmpty()) {
            return;
        }
        LocalDateTime recommendationCreate = recommendation.get().getCreatedAt();
        if (!recommendationCreate.isAfter(LocalDateTime.now().minusMonths(6))) {
            throw new DataValidationException("Recommendation duration has not expired");
        }
    }

    private Skill getSkill(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not exist"));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    public Recommendation getRecommendation(long recommendationId) {
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataValidationException("Recommendation not found"));
    }
}
