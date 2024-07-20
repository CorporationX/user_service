package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@Tag(name = "Контроллер событий", description = "Выполняет действия, связанные с событиями.")
public class EventController {
    private final EventService eventService;

    // Создать событие
    @PostMapping("")
    @Operation(summary = "Создание события",
            description = "Позволяет создать новое событие. " +
                    "Для создания события необходимо передать ДТО события.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Событие успешно создано!"),
            @ApiResponse(responseCode = "400", description = "Событие не было создано!"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера!",
                    content = @Content)
    })
    public ResponseEntity<EventDto> create(@RequestBody EventDto event) {
        if (validateEventDto(event)) {
            return ResponseEntity.ok(eventService.create(event));
        }
        throw new DataValidationException("Не удалось создать событие!" +
                " Введены не верные данные.");
    }

    // получить событие
    @GetMapping("/{id}")
    @Operation(summary = "Получение события.",
            description = "Позволяет получить событие по его идентификатору.")
    public ResponseEntity<EventDto> getEvent(@PathVariable("id")
                                             @Parameter(name = "АйДи",
                                                     description = "Идентификатор события.",
                                                     required = true,
                                                     example = "33") long eventId) {
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }

    public boolean validateEventDto(EventDto event) {
        return !event.getTitle().isEmpty()
                && !event.getTitle().isBlank()
                && event.getTitle().length() <= 64
                && event.getStartDate() != null
                && event.getOwnerId() != 0;
    }

    //Получить все события с фильтрами
    @PostMapping("/filtered")
    @Operation(summary = "Получение всех событий с фильтрами.",
            description = "Позволяет получить все события, удовлетворяющие переданному " +
                    "фильтру. Фильтр передаётся в теле запроса.")
    public ResponseEntity<List<EventDto>> getEventsByFilter(
            @RequestBody EventFilterDto filter) {
        return ResponseEntity.ok(eventService.getEventsByFilter(filter));
    }

    // Удалить событие
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление события.", description = "Удаляет событие по его" +
            " идентификатору.")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id")
                                            @Parameter(name = "АйДи",
                                                    description = "Идентификатор события.",
                                                    required = true) long idEvent) {
        eventService.deleteEvent(idEvent);
        return ResponseEntity.ok().build();
    }

    //Обновить событие
    @PutMapping("")
    @Operation(summary = "Обновление события",
            description = "Обновляет событие в соответствие с переданным в теле запроса" +
                    " ДТО события.")
    public ResponseEntity<EventDto> updateEvent(@RequestBody EventDto event) {
        if (event.getTitle().isBlank()
                || event.getStartDate().toString().isBlank()
                || event.getOwnerId() == null) {
            throw new DataValidationException("Событие не прошло согласование!");
        }
        return ResponseEntity.ok(eventService.updateEvent(event));
    }

    // Получить все созданные пользователем события
    @GetMapping("/created/all")
    @Operation(summary = "Получение всех событий, созданных данным пользователем",
            description = "Получает все события, созданные пользователем по идентификатору " +
                    "пользователя.")
    public ResponseEntity<List<EventDto>> getOwnedEvents(
            @RequestParam("userId")
            @Parameter(name = "АйДи",
                    description = "Идентификатор пользователя.",
                    required = true) long userId) {
        return ResponseEntity.ok(eventService.getOwnedEvents(userId));
    }

    // Получить все события, в которых пользователь принимает участие
    @GetMapping("/participated/all")
    @Operation(summary = "Получение всех событий, в которых пользователь принимает участие",
            description = "Позволяет получить все события, в которых участвует пользователь" +
                    " по идентификатору пользователя.")
    public ResponseEntity<List<EventDto>> getParticipatedEvents(
            @RequestParam("userId")
            @Parameter(name = "АйДи",
                    description = "Идентификатор пользователя.",
                    required = true) long userId) {
        return ResponseEntity.ok(eventService.getParticipatedEvents(userId));
    }
}
