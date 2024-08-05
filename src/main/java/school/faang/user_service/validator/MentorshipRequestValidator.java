package school.faang.user_service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

@Component
@AllArgsConstructor
public class MentorshipRequestValidator {
    private final UserService userService;

    public boolean validateMentorshipRequest(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getRequesterId();
        long receiverId = mentorshipRequestDto.getReceiverId();
        checkUsersExisting(requesterId);
        checkUsersExisting(receiverId);
        checkRequesterIsNotEqualsReceiver(requesterId, receiverId);
        checkDescription(mentorshipRequestDto);
        checkRecommendationRequestStatus(mentorshipRequestDto.getStatus());
        return true;
    }

    public boolean checkDates(MentorshipRequestDto mentorshipRequestDto) {
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeRequest = mentorshipRequestDto.getCreatedAt();
        int compareResult = timeRequest.plusMonths(3).compareTo(timeNow);
        if (compareResult >= 0) {
            throw new RuntimeException("Заявка уже была оформлена");
        }
        return true;
    }
  
    private boolean checkDescription(MentorshipRequestDto mentorshipRequestDto) {
        if(mentorshipRequestDto.getDescription() == null && mentorshipRequestDto.getDescription().isBlank()){
           throw new DataValidationException("Description can't be blank");
        }
        return true;
    }
    
    private void checkUsersExisting(Long userId) {
        userService.existsById(userId);
    }

    private void checkRequesterIsNotEqualsReceiver(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            throw new DataValidationException("you can't be mentor for yourself");
        }
    }

    public boolean checkRecommendationRequestStatus(RequestStatus status) {
        if (status == RequestStatus.ACCEPTED || status == RequestStatus.REJECTED) {
            throw new DataValidationException(String.format("recommendation request has already %s", status));
        }
        return true;
    }
}