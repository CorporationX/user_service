package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SkillOfferValidator {
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;

    public void validate(SkillOfferDto dto) {
        if (dto.getSkill() != null && !skillOfferRepository.existsById(dto.getSkill())) {
            throw new DataValidationException("Selected skill doesn't exist");
        }
        if (dto.getReceiverId() != null && !userRepository.existsById(dto.getReceiverId())) {
            throw new DataValidationException("Selected receiver doesn't exist");
        }

        if (Objects.equals(dto.getAuthorId(), dto.getReceiverId())) {
            throw new DataValidationException("Author and receiver can't be the same user");
        }
    }
}