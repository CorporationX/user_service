package school.faang.user_service.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
@RequestMapping("/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("users/{userId}/mentees")
    public ResponseEntity<List<UserDto>> getMentees(
            @PathVariable @NotNull(message = "id field is required ")
            @Positive(message = "UserId must be greater than 0 ")
            Long userId) {
        log.info("Getting list of user mentees with id {} ", userId);

        return ResponseEntity.ok(mentorshipService.getMentees(userId));
    }

    @GetMapping("users/{userId}/mentors")
    public ResponseEntity<List<UserDto>> getMentors(
            @PathVariable @NotNull(message = "id field is required ")
            @Positive(message = "UserId must be greater than 0 ")
            Long userId) {
        log.info("Getting list of user mentors with id {} ", userId);

        return ResponseEntity.ok(mentorshipService.getMentors(userId));
    }

    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public ResponseEntity<Void> deleteMentee(
            @PathVariable @NotNull(message = "id field is required ")
            @Positive(message = "MentorId must be greater than 0 ")
            Long mentorId,
            @PathVariable @NotNull(message = "id field is required ")
            @Positive(message = "MenteeId must be greater than 0 ")
            Long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
        log.info("Removing a mentee with  Id {} from the list of a mentor with Id {} ", menteeId, mentorId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    public ResponseEntity<Void> deleteMentor(
            @PathVariable @NotNull(message = "id field is required ")
            @Positive(message = "MenteeId must be greater than 0 ")
            Long menteeId,
            @PathVariable @NotNull(message = "id field is required ")
            @Positive(message = "MentorId must be greater than 0 ")
            Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
        log.info("Removing a mentor with  Id {} from the list of a mentee with Id {} ", mentorId, menteeId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
