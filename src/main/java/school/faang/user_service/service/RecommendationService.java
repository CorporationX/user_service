package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationMapper;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;

    @Autowired
    public RecommendationService(RecommendationRepository recommendationRepository,
                                 SkillOfferRepository skillOfferRepository,
                                 RecommendationMapper recommendationMapper,
                                 SkillRepository skillRepository) {
        this.recommendationRepository = recommendationRepository;
        this.skillOfferRepository = skillOfferRepository;
        this.skillRepository = skillRepository;
        this.recommendationMapper = recommendationMapper;
    }

    public void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isEmpty()) {
            throw new DataValidationException("recommendation cannot be empty");
        }
    }

    // можно сделать так
//    public Long create(RecommendationDto recommendationDto) {
//        Recommendation recommendation = recommendationMapper.recommendationDtoToRecommendation(recommendationDto);
//        Recommendation savedRecommendation = recommendationRepository.save(recommendation);
//        return savedRecommendation.getId();
//    }

    public Long create(RecommendationDto recommendationDto) {

        validateRecommendation(recommendationDto);

        Optional<Recommendation> optional = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId());

        optional.ifPresent(lastRecommendation -> {
            if (lastRecommendation.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6))) {
                throw new DataValidationException("the recommendation can be given only after 6 months!");
            }
        });

        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffers();

        skillOfferDtoList.forEach(skillOfferDto -> {
            if (skillExists(skillOfferDto.getSkillTitle())) {
                skillOfferRepository.create(skillOfferDto.getSkillId(), skillOfferDto.getRecommendationId());
            }
        });

        List<Skill> skillsByReceiverId = skillRepository.findAllByUserId(recommendationDto.getReceiverId());

        for (Skill skill : skillsByReceiverId) {
            for (SkillOfferDto skillOfferDto : recommendationDto.getSkillOffers()) {
                if (skillOfferDto.getSkillId() == skill.getId()) {
                    // TODO: Добавить автора рекомендации гарантом к скиллу, который он предлагает (UserSkillGuaranteeRepository еще пустой)
                }
            }

        }

        return recommendationRepository.create(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
    }

    /**
     * Проверяет существует ли предложенный навык в системе по заголовку.
     *
     * @param title заголовок навыка.
     * @return Возвращает true если существует
     */
    private boolean skillExists(String title) {
        return skillRepository.existsByTitle(title);
    }


}
