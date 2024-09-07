package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.UserMentorshipDto;
import school.faang.user_service.mapper.UserMentorshipMapper;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final UserMentorshipMapper userMentorshipMapper;


    @GetMapping("/{userId}/mentees")
    public Collection<UserMentorshipDto> getMentees(@PathVariable("userId") Long userId) {
        return userMentorshipMapper.toDtoList(mentorshipService.getMentees(userId));
    }

    @GetMapping("/{userId}/mentors")
    public Collection<UserMentorshipDto> getMentors(@PathVariable("userId") Long userId) {
        return userMentorshipMapper.toDtoList(mentorshipService.getMentors(userId));
    }

    @DeleteMapping("/{mentorId}/mentees")
    public void deleteMentee(@PathVariable("mentorId") Long mentorId, @RequestParam("menteeId") Long menteeId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/{menteeId}/mentors")
    public void deleteMentor(@PathVariable("menteeId") Long menteeId, @RequestParam("mentorId") Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
