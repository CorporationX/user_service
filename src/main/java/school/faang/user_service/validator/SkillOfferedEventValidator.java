package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventSkillOfferedDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Component
@RequiredArgsConstructor
public class SkillOfferedEventValidator {
    private final SkillOfferRepository skillOfferRepository;
    public void validate(EventSkillOfferedDto dto) {
        if (dto == null) {
            throw new DataValidationException("EventSkill  can't be null");
        }

        if (dto.getAuthorId() == null || dto.getAuthorId() <= 0) {
            throw new DataValidationException("AuthorId must be a positive number");
        }

        if (dto.getReceiverId() == null || dto.getReceiverId() <= 0) {
            throw new DataValidationException("ReceiverId must be a positive number");
        }

        if (dto.getSkillOfferedId() <= 0) {
            throw new DataValidationException("SkillOfferedId must be a positive number");
        }

        if (dto.getAuthorId() == dto.getReceiverId()) {
            throw new DataValidationException("Author and receiver can't be the same user");
        }

        if (!skillOfferRepository.existsById(dto.getSkillOfferedId())) {
            throw new DataValidationException("Skill doesn't exist in the system");
        }
    }
}