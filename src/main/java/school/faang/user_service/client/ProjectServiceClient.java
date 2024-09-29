package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.controller.event.CalendarEventDto;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}")
public interface ProjectServiceClient {
    @PostMapping("/projects/{projectId}/calendars/events")
    String createGoogleCalendarEvent(@PathVariable long projectId,
                                     @RequestParam String calendarId,
                                     @RequestBody CalendarEventDto eventDto);
}

