package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Tag(name = "Mentorship", description = "Управление связями ментор-менти")
public class MentorshipController {

    private final MentorshipService mentorshipService;
    private final UserContext userContext;

    @PostMapping("/{mentorId}/{menteeId}")
    @Parameter(
            name = "x-user-id",
            description = "ID текущего пользователя",
            required = true,
            in = ParameterIn.HEADER,
            example = "123"
    )
    @Operation(
            summary = "Добавить связь ментор-менти",
            description = "Создаёт связь между ментором и менти. "
                    + "Только сам ментор может добавить связь.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Связь успешно создана")
            }
    )
    public ResponseEntity<Void> addMentorship(@PathVariable long mentorId, @PathVariable long menteeId) {

        long currentUserId = userContext.getUserId();

        if (currentUserId != mentorId && currentUserId != menteeId) {
            log.warn("Access denied for userId={} trying to add mentorship between mentorId={} and menteeId={}",
                    currentUserId, mentorId, menteeId);
            throw new ForbiddenException("Доступ запрещен");
        }

        if (mentorId == menteeId) {
            log.warn("Invalid mentorship request: mentorId equals menteeId={}", mentorId);
            throw new DataValidationException("Вы не можете выбрать себя");
        }

        mentorshipService.addMentorship(mentorId, menteeId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{mentorId}/{menteeId}")
    @Parameter(
            name = "x-user-id",
            description = "ID текущего пользователя",
            required = true,
            in = ParameterIn.HEADER,
            example = "123"
    )
    @Operation(
            summary = "Удалить связь ментор-менти",
            description = "Удаляет существующую связь между ментором и менти. "
                    + "Только сам ментор или менти может удалить связь."
    )
    public ResponseEntity<Void> deleteMentorship(@PathVariable long mentorId, @PathVariable long menteeId) {

        long currentUserId = userContext.getUserId();

        if (currentUserId != mentorId && currentUserId != menteeId) {
            log.warn("Access denied for userId={} trying to delete mentorship between mentorId={} and menteeId={}",
                    currentUserId, mentorId, menteeId);
            throw new ForbiddenException("Доступ запрещен");
        }

        if (mentorId == menteeId) {
            log.warn("Invalid delete mentorship request: mentorId equals menteeId={}", mentorId);
            throw new DataValidationException("Вы не можете выбрать себя");
        }

        mentorshipService.deleteMentorship(mentorId, menteeId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mentee/{userId}")
    @Parameter(
            name = "x-user-id",
            description = "ID текущего пользователя",
            required = true,
            in = ParameterIn.HEADER,
            example = "123"
    )
    @Operation(
            summary = "Получить список менти",
            description = "Возвращает всех менти (учеников) указанного пользователя (ментора)."
    )
    public List<UserDto> getMentees(@PathVariable long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentor/{userId}")
    @Parameter(
            name = "x-user-id",
            description = "ID текущего пользователя",
            required = true,
            in = ParameterIn.HEADER,
            example = "123"
    )
    @Operation(
            summary = "Получить список менторов",
            description = "Возвращает всех менторов указанного пользователя (менти)."
    )
    public List<UserDto> getMentors(@PathVariable long userId) {
        return mentorshipService.getMentors(userId);
    }
}