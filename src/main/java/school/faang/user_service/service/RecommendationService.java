package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.mappers.RecommendationMapper;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.mappers.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationValidator recommendationValidator;
    private final SkillValidator skillValidator;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final SkillMapper skillMapper;


    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        validate(recommendation);

        long newRecommendationId = recommendationRepository.create(recommendation.getAuthorId(), recommendation.getReceiverId(),
                recommendation.getContent());

        Recommendation recommendationEntity = recommendationRepository.findById(newRecommendationId)
                .orElseThrow(() -> new DataValidationException("Recommendation not found"));

        saveSkillOffers(recommendationEntity, recommendation.getSkillOffers());
        return recommendationMapper.toDto(recommendationEntity);
    }

    public RecommendationDto update(RecommendationDto recommendationUpdate) {
        validate(recommendationUpdate);
        Recommendation recommendationEntity = recommendationRepository.update(recommendationUpdate.getAuthorId(), recommendationUpdate.getReceiverId(), recommendationUpdate.getContent());

        skillOfferRepository.deleteAllByRecommendationId(recommendationEntity.getId());
        saveSkillOffers(recommendationEntity, recommendationUpdate.getSkillOffers());
        return recommendationMapper.toDto(recommendationEntity);
    }

    private void validate(RecommendationDto recommendation) {
        recommendationValidator.validateRecommendationContent(recommendation);
        recommendationValidator.validateRecommendationTerm(recommendation);
        skillValidator.validateSkillOffersDto(recommendation);
    }

    @Transactional
    public void saveSkillOffers(Recommendation recommendationEntity, List<SkillOfferDto> skillOffers) {
        for (SkillOfferDto skillOfferDto : skillOffers) {
            long newSkillOfferId = skillOfferRepository.create(
                    skillOfferDto.getSkill(),
                    recommendationEntity.getId()
            );
            recommendationEntity.addSkillOffer(skillOfferRepository.findById(newSkillOfferId).orElseThrow(() -> new DataValidationException("Skill not found")));

            List<Skill> skillsInUser = skillRepository.findAllByUserId(recommendationEntity.getReceiver().getId());
            List<SkillDto> skillsInUserDtos = skillsInUser.stream().map(skillMapper::toDto).toList();

            UserSkillGuaranteeDto userSkillGuaranteeDto = UserSkillGuaranteeDto.builder()
                    .userId(recommendationEntity.getReceiver().getId())
                    .guarantorId(recommendationEntity.getAuthor().getId())
                    .build();


            for (SkillOffer skillOffer : recommendationEntity.getSkillOffers()) {
                if (skillsInUserDtos.contains(skillOffer.getSkill())) {
                    for (SkillDto skill : skillsInUserDtos) {
                        provideGuarantee(skill, recommendationEntity.getAuthor().getId(), userSkillGuaranteeDto);
                    }
                }
            }
        }
    }

    private void provideGuarantee(SkillDto skill, long authorId, UserSkillGuaranteeDto userSkillGuaranteeDto) {
        if (skill.getGuarantees().stream()
                .noneMatch(guarantee -> guarantee.getGuarantorId() == authorId)) {
            userSkillGuaranteeDto.setSkillId(skill.getId());
            skill.getGuarantees().add(userSkillGuaranteeDto);
            userSkillGuaranteeRepository.save(userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto));
        }
    }
}
