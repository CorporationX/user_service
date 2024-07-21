package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidation {
    private final int HALF_OF_YEAR = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillOfferRepository skillOfferRepository;

    public void recommendationIsNotEmpty(RecommendationDto recommendation) {

        if (recommendation.content().isEmpty()) {
            throw new DataValidationException("Content cannot be empty");
        }

        if (recommendation.content().isBlank()) {
            throw new DataValidationException("Content cannot be empty");
        }

    }

    public void validationHalfOfYear(RecommendationDto recommendation) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findLatestPendingRequest(recommendation.authorId(),
                recommendation.receiverId());

        request.ifPresent(s -> {
            if (s.getCreatedAt().plusMonths(HALF_OF_YEAR).isAfter(LocalDateTime.now())) {
                throw new DataValidationException("Not be half of year.");
            }
        });
    }

    public void skillValid(List<SkillOfferDto> skillOfferDtoList) {
        for (SkillOfferDto skillOfferDto : skillOfferDtoList) {
            if (!skillOfferRepository.existsById(skillOfferDto.skillId())){
                throw new DataValidationException("Not found skill");
            }
        }
    }
}

