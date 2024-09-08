package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private SkillOfferRepository skillOfferRepository;

    public RecommendationDto create(RecommendationDto recommendation) {
        if (hasRecommendationInLastSixMonths(recommendation.getAuthorId(), recommendation.getReceiverId())) {
            throw new DataValidationException("Recommendation cannot be given more than once in 6 months.");
        }

        for (SkillOfferDto offer : recommendation.getSkillOffers()) {
            if (!skillOfferRepository.existsById(offer.getSkillId())) {
                throw new DataValidationException("One of the skills does not exist.");
            }
        }

        recommendation.setCreatedAt(LocalDateTime.now());

        Recommendation createdRecommendation = recommendationRepository.findById(recommendation.getId()).orElseThrow();
        return mapToDto(createdRecommendation);
    }

    private RecommendationDto mapToDto(Recommendation recommendation) {
        RecommendationDto dto = new RecommendationDto();
        dto.setId(recommendation.getId());
        dto.setAuthorId(recommendation.getAuthor().getId());
        dto.setReceiverId(recommendation.getReceiver().getId());
        dto.setContent(recommendation.getContent());
        dto.setCreatedAt(recommendation.getCreatedAt());

        List<SkillOfferDto> skillOffers = skillOfferRepository.findAllByUserId(recommendation.getReceiver().getId())
                .stream()
                .map(skillOffer -> {
                    SkillOfferDto offerDto = new SkillOfferDto();
                    offerDto.setId(skillOffer.getId());
                    offerDto.setSkillId(skillOffer.getSkill().getId());
                    return offerDto;
                })
                .collect(Collectors.toList());

        dto.setSkillOffers(skillOffers);
        return dto;
    }

    public boolean hasRecommendationInLastSixMonths(long authorId, long receiverId) {
        // Получаем последнюю рекомендацию от автора к получателю
        Optional<Recommendation> existingRecommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                authorId, receiverId);

        if (existingRecommendation.isPresent()) {
            // Проверяем, была ли рекомендация сделана в последние 6 месяцев
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
            return existingRecommendation.get().getCreatedAt().isAfter(sixMonthsAgo);
        }

        // Если рекомендации нет, возвращаем false
        return false;
    }







//    @Autowired
//    public RecommendationService(RecommendationRepository recommendationRepository, SkillOfferRepository skillOfferRepository) {
//        this.recommendationRepository = recommendationRepository;
//        this.skillOfferRepository = skillOfferRepository;
//    }
//
//    public RecommendationDto create(RecommendationDto recommendation) {
//        validateRecommendation(recommendation);
//        return saveRecommendation(recommendation);
//    }
//
//    private void validateRecommendation(RecommendationDto recommendation) {
//        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
//        List<RecommendationDto> recentRecommendations = recommendationRepository.findByAuthorIdAndReceiverIdAndCreatedAtAfter(
//                recommendation.getAuthorId(), recommendation.getReceiverId(), sixMonthsAgo);
//        if (!recentRecommendations.isEmpty()) {
//            throw new DataValidationException("Cannot give another recommendation to this user within 6 months");
//        }
//    }
}
