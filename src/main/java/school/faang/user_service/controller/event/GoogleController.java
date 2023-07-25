package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.service.event.GoogleCalendarService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/calendar/{id}")
    public ResponseEntity<?> createEvent(@PathVariable("id") Long id) throws IOException {
        idValidation(id);
        return googleCalendarService.createEvent(id);
    }

    private void idValidation(Long id) {
        if (id <= 0 || null == id) {
            throw new BadRequestException("Id must be greater than 0");
        }
    }
}
