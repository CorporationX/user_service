package school.faang.user_service.controller.mentorship;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(Long userId){
        if(userId == null){
            throw new NullPointerException("Userid " + userId + " is null");
        }
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(Long userId){
        if(userId == null){
            throw new NullPointerException("Userid " + userId + " is null");
        }
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(Long menteeId, Long mentorId){
        mentorshipService.deleteMentee(menteeId, mentorId);
    }
}
