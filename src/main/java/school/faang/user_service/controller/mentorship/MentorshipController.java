package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/mentorship", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void addMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipDto) {
        mentorshipService.addMentorship(mentorshipDto.mentorId(), mentorshipDto.menteeId());
    }

    @GetMapping("/{userId}/mentees")
    public List<UserDto> getMentees(@PathVariable Long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{userId}/mentors")
    public List<UserDto> getMentors(@PathVariable Long userId) {
        return mentorshipService.getMentors(userId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteMentorship(@PathVariable Long userId, @RequestParam Long mentorId) {
        mentorshipService.deleteMentorship(mentorId, userId);
    }
}
