package school.faang.user_service.validator.skill;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class SkillOfferValidator {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public void validateSkillOffer(
        Long senderId,
        Long receiverId,
        Long skillId
    ) {
        if (!userRepository.existsById(senderId)) {
            throw new EntityNotFoundException("Sender not found with ID: %d".formatted(senderId));
        }
        if (!userRepository.existsById(receiverId)) {
            throw new EntityNotFoundException("Receiver not found with ID: %d".formatted(receiverId));
        }
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException("Skill not found with ID: %d".formatted(skillId));
        }
    }
}
