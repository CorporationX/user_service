package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.REQUEST_MENTORSHIP)
@Tag(name = "Пример контроллера", description = "Описание контроллера")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    @Operation(summary = "Приветственный метод 111", description = "Возвращает приветственное сообщение 1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция успешно заершена"),
            @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @GetMapping
    @Operation(summary = "Приветственный метод 222", description = "Возвращает приветственное сообщение 2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция успешно заершена"),
            @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@RequestBody(required = false) RequestFilterDto filters) {
        return ResponseEntity.status(HttpStatus.OK).body(mentorshipRequestService.getRequests(filters));
    }

    @PatchMapping("/{id}/accept")
    @Operation(summary = "Приветственный метод 333", description = "Возвращает приветственное сообщение 3")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция успешно заершена"),
            @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    public ResponseEntity<MentorshipRequestDto> acceptMentorship(@PathVariable("id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(mentorshipRequestService.acceptRequest(id));
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Приветственный метод 444", description = "Возвращает приветственное сообщение 4")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция успешно заершена"),
            @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    public ResponseEntity<MentorshipRequestDto> rejectMentorship(@PathVariable("id") long id, @RequestBody @Valid RejectionDto rejectionDto) {
        return ResponseEntity.status(HttpStatus.OK).body(mentorshipRequestService.rejectRequest(id, rejectionDto));
    }
}
