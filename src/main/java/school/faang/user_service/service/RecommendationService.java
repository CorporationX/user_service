package school.faang.user_service.service;

import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utils.validator.ValidatorService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final ValidatorService validatorService;
    private final RecommendationMapper recommendationMapper;
    public void create(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendationDto.getAuthorId(), recommendationDto.getReceiverId())
                .orElseThrow(()-> new DataValidationException("Recommendation is not found"));

        validatorService.validateTime(recommendation);

//        for (SkillOfferDto skillOfferDto : recommendationDto.getSkillOffers()) {
//            for (SkillOffer skillOffer : skillOfferRepository.findAllOffersOfSkill(skillOfferDto.getSkillId(), skillOfferDto.getSkillId())) {
//                skillOffer.contains(recommendationMapper.toEntity(skillOfferDto);
//            }
//        }

    }
}
