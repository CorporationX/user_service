package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void create(RecommendationDto recommendation){
        long authorId = recommendation.getAuthorId();
        long userId = recommendation.getReceiverId();

        Optional<Recommendation> recommend = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId,userId);
        if(recommend.isPresent()) {
            LocalDateTime updateTime = recommend.get().getUpdatedAt();
            LocalDateTime now = LocalDateTime.now().minusMonths(6);
            if(now.isAfter(updateTime)){
                throw new DataValidationException("");
            }
        }

        List<SkillOfferDto> skillsOffersDto = recommendation.getSkillOffers();

        boolean b = skillsOffersDto.stream()
                .noneMatch(s -> skillRepository.existsByTitle(s.getSkill().getTitle()));

        if(b){
            throw new DataValidationException("");
        }


    }
}
