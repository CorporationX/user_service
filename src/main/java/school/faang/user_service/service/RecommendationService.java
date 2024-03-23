package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validateLastRecommendationDate(recommendationDto);
        var newRecommendation = recommendationRepository.save(recommendationMapper.toEntity(recommendationDto));

        return recommendationMapper.toDto(newRecommendation);
    }

    private RecommendationDto getLastRecommendation(RecommendationDto recommendation) throws EntityNotFoundException {
        User author = userRepository.findById(recommendation.getAuthorId()).orElseThrow(
                () -> new EntityNotFoundException("This author does not exist."));
        User receiver = userRepository.findById(recommendation.getReceiverId()).orElseThrow(
                () -> new EntityNotFoundException("This receiver does not exist."));
        List<Recommendation> recommendationsToUser = author.getRecommendationsGiven()
                .stream().filter(rec -> rec.getReceiver().getId() == receiver.getId())
                .toList();

        if (recommendationsToUser.isEmpty()) {
            throw new EntityNotFoundException("This user does not have recommendations");
        }

        return recommendationMapper.toDto(recommendationsToUser.get(recommendationsToUser.size() - 1));
    }

    private void validateLastRecommendationDate(RecommendationDto recommendation) throws DataValidationException {
        var lastRecommendationDate = getLastRecommendation(recommendation).getCreatedAt();

        if (recommendation.getCreatedAt().minusMonths(6).isBefore(
                lastRecommendationDate)) {
            throw new DataValidationException(
                    "Last recommendation should be created at least 6 months before the new one.");
        }
    }
}