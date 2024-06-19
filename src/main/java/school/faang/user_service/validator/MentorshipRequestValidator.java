package school.faang.user_service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;

@AllArgsConstructor
@Component
public class MentorshipRequestValidator {
    private final UserService userService;

    public boolean validateRequest(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestDto.getDescription() != null && !mentorshipRequestDto.getDescription().isBlank();
    }

    public boolean checkIdAndDates(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getUserId().equals(mentorshipRequestDto.getMentorId())) {
            throw new RuntimeException("Пользователь не может быть ментором для себя");
        }
        if (!checkRequestDate(mentorshipRequestDto)) {
            throw new RuntimeException("Заявка уже была оформлена");
        }
        return true;
    }

    private boolean checkRequestDate(MentorshipRequestDto mentorshipRequestDto) {
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeRequest = mentorshipRequestDto.getCreatedAt();
        int compareResult = timeRequest.plusMonths(3).compareTo(timeNow);
        return compareResult <= 0;
    }

    public boolean checkUserAndMentorExists(Long userId, Long mentorId) {
        return userService.existsById(userId) && userService.existsById(mentorId);
    }

}