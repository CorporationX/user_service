package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;


@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/users")
@Tag(name = "Контроллер для управления менторами и менти")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/{Id}/mentees/")
    @Operation(
            summary = "Получить менти",
            description = "Позволяет получить менти пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentees  found"),
            @ApiResponse(responseCode = "404", description = "Mentees  not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<UserDto> getMentees(@PathVariable("Id")
                                    @Parameter(description = "Идентификатор пользователя менти которого мы хотим получить")
                                    long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{Id}/mentors")
    @Operation(
            summary = "Получить менторов",
            description = "Позволяет получить менторов пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentors  found"),
            @ApiResponse(responseCode = "404", description = "Mentors  not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<UserDto> getMentors(@PathVariable("Id")
                                    @Parameter(description = "Идентификатор пользователя ментора которого мы хотим получить")
                                    long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/{Id}/mentees/{menteeId}")
    @Operation(
            summary = "Удалить менти",
            description = "Позволяет удалить менти пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentee was deleted"),
            @ApiResponse(responseCode = "404", description = "Mentee  wasn't deleted "),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void deleteMentee(@PathVariable
                             @Parameter(description = "Идентификатор менти которого нужно удалить")
                             long menteeId,

                             @PathVariable("Id")
                             @Parameter(description = "Идентификатор пользователя у которого нужно удалить менти")
                             long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/{Id}/mentors/{mentorId}")
    @Operation(
            summary = "Удалить ментора",
            description = "Позволяет удалить ментора пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor was deleted"),
            @ApiResponse(responseCode = "404", description = "Mentor  wasn't deleted "),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void deleteMentor(@PathVariable("Id")
                             @Parameter(description = "Идентификатор менти у которого нужно удалить ментора")
                             long menteeId,

                             @PathVariable
                             @Parameter(description = "Идентификатор ментора которого нужно удалить")
                             long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
