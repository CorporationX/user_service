package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private final int MIN_MONTHS_FOR_NEW_RECOMMENDATION = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void validateData(RecommendationDto dto) {
        validateContent(dto.getContent());
        checkDuplicateSkills(dto.getSkillOffers());
        checkExistAllSkills(dto.getSkillOffers());
        checkAuthorAndReceiverId(dto.getAuthorId(), dto.getReceiverId());
        if (dto.getSkillOffers() == null) {
            dto.setSkillOffers(Collections.emptyList());
        }
    }

    public void validateContent(String content) {
        if (content == null) {
            throw new DataValidationException("Null content in recommendation");
        }
        if (content.isBlank()) {
            throw new DataValidationException("Empty content in recommendation");
        }
    }

    public void checkDate(RecommendationDto dto) {
        recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(dto.getAuthorId(), dto.getReceiverId())
                .ifPresent(lastRecommendation -> {
                    LocalDateTime today = LocalDateTime.now();
                    boolean isBeforeMinMonths = today.minusMonths(MIN_MONTHS_FOR_NEW_RECOMMENDATION)
                            .isBefore(lastRecommendation.getCreatedAt());

                    if (isBeforeMinMonths) {
                        throw new DataValidationException("User can make recommendation no more than once every six months to one and the same person");
                    }
                });
    }

    public void checkDuplicateSkills(List<SkillOfferDto> skills) {
        Set<SkillOfferDto> setOfSkills = new HashSet<>(skills);
        if (setOfSkills.size() != skills.size()) {
            throw new DataValidationException("Duplicate skill");
        }
    }

    public void checkExistAllSkills(List<SkillOfferDto> skills) {
        List<Long> skillsId = skills.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        int countExistingSkills = skillRepository.countExisting(skillsId);
        if (skills.size() != countExistingSkills) {
            throw new DataValidationException("Id of a non-existing skill has been sent");
        }
    }

    public void checkAuthorAndReceiverId(long authorId, long receiverId) {
        if (authorId == receiverId) {
            throw new DataValidationException("You can't make a recommendation to yourself");
        }
    }
}