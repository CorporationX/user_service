package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.CreateEventRequestDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.dto.event.UpdateEventRequestDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;


@Tag(name = "Events", description = "Api for events management")
@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventController {
  private final EventService eventService;

    @Operation(summary = "Create new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "event created successfully",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EventResponseDto.class)
                    )}),
            @ApiResponse(responseCode = "400", description = "invalid request data")
    })
    @PostMapping
    public EventResponseDto createEvent(@Valid @RequestBody CreateEventRequestDto createRequest) {
        return eventService.createEvent(createRequest);
    }

    @Operation(summary = "get event by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "event received successfully",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EventResponseDto.class)
                    )}),
    @ApiResponse(responseCode = "400", description = "invalid request data"),
    @ApiResponse(responseCode = "404", description = "event with received id not found")})
    @GetMapping("/{eventId}")
    public EventResponseDto getEvent(@PathVariable @Valid @Positive Long eventId) {
        return eventService.getEvent(eventId);
    }

    @Operation(summary = "get event with filters")
    @ApiResponses(value = {
    @ApiResponse(
            responseCode = "200", description = "events received successfully",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EventResponseDto.class))
                    )}),
            @ApiResponse(responseCode = "400", description = "invalid request data")
    })
    @PostMapping("/filter")
        public List<EventResponseDto> filterEvents(@Valid @RequestBody EventFilterDto filterDto) {
        return eventService.getEventsByFilters(filterDto);
    }


    @Operation(summary = "delete event by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "event deleted successfully"),
            @ApiResponse(responseCode = "400", description = "invalid request data"),
            @ApiResponse(responseCode = "404", description = "event with received id not found")})
    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable @Positive Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @Operation(summary = "edit event by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "event edited successfully",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EventResponseDto.class)
                    )}),
            @ApiResponse(responseCode = "400", description = "invalid request data"),
            @ApiResponse(responseCode = "404", description = "event or user with received id not found")})
    @PatchMapping("/{eventId}")
    public EventResponseDto updateEvent(
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody UpdateEventRequestDto updateRequest) {
        updateRequest.setId(eventId);
        return eventService.updateEvent(updateRequest);
    }

    @Operation(summary = "get events by owner")
    @ApiResponses(value = {
    @ApiResponse(
            responseCode = "200", description = "events received successfully",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EventResponseDto.class))
                    )}),
            @ApiResponse(responseCode = "400", description = "invalid request data")
    })
    @GetMapping("/owner/{userId}")
    public List<EventResponseDto> getEventsByOwner(@PathVariable @Valid @Positive Long userId) {
        return eventService.getEventsByOwner(userId);
    }

    @Operation(summary = "get events by participant")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "events received successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = EventResponseDto.class))
                            )}),
            @ApiResponse(responseCode = "400", description = "invalid request data")
    })
    @GetMapping("/participant/{userId}")
    public List<EventResponseDto> getEventsByParticipant(@PathVariable @Valid @Positive Long userId) {
        return eventService.getEventsByParticipant(userId);
    }
}