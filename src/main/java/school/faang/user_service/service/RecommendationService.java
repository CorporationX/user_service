package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;

    public RecommendationDto create(RecommendationDto recommendation) {
        validateLastRecommendationDate(recommendation);
        //  get recommendationRepository.save(recommendation (of Entity)) -> result;
        //  return: result.toDto()
    }

    private void getLastRecommendation(RecommendationDto recommendation) throws EntityNotFoundException {
        User author = userRepository.findById(recommendation.getAuthorId()).orElseThrow(
                () -> new EntityNotFoundException("This author does not exist."));
        User receiver = userRepository.findById(recommendation.getReceiverId()).orElseThrow(
                () -> new EntityNotFoundException("This receiver does not exist."));
        List<Recommendation> recommendationsToUser = author.getRecommendationsGiven()
                .stream().filter(rec -> rec.getReceiver().getId() == receiver.getId())
                .toList();
        if (validateAuthorRecommendationsList(recommendationsToUser)) {
            Recommendation latestRecommendation = recommendationsToUser.get(recommendationsToUser.size() - 1);
        }
    }

    private boolean validateAuthorRecommendationsList(List<Recommendation> recommendationsToUser) {
        return !recommendationsToUser.isEmpty();
    }

    private void validateLastRecommendationDate(RecommendationDto lastRecommendation) throws DataValidationException {
        if (LocalDateTime.now().minusMonths(6).isBefore(
                lastRecommendation.getCreatedAt())) {
            throw new DataValidationException(
                    "Last recommendation should be created at least 6 months before the new one.");
        }
    }
}