package school.faang.user_service.validator.mentorship.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {

    private final UserRepository userRepo;
    private final SkillRequestRepository skillRequestRepo;

    public void validate(RecommendationRequestDto dto) {
        LocalDateTime sixMonthPeriod = LocalDateTime.now().minusMonths(6);
        if (dto.getMessage().isEmpty() || dto.getMessage().isBlank()) {
            throw new DataValidationException("Message cannot be empty or blank");
        }
        if (!userRepo.existsById(dto.getRequesterId())) {
            throw new DataValidationException("Requester user does not exist");
        }
        if (!userRepo.existsById(dto.getRecieverId())) {
            throw new DataValidationException("Reciever user does not exist");
        }
        if (dto.getRequesterId() == dto.getRecieverId()) {
            throw new DataValidationException("Requester ID and reciever ID cannot be the same");
        }
        if (dto.getCreatedAt().isAfter(sixMonthPeriod)) {
            throw new DataValidationException("A recommendation request from the same user to another " +
                    "can be sent no more than once every 6 months.");
        }
    }

    public void validateSkills(RecommendationRequestDto dto) {
        for (Skill skill : dto.getSkills()) {
            if (!skillRequestRepo.existsById(skill.getId())) {
                throw new DataValidationException("Requested skills do not exist in the DB");
            }
        }
    }
}
