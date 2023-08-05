package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.GoogleCalendarService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar")
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/{id}")
    public ResponseEntity<?> createEvent(@PathVariable("id") Long id) throws IOException {
        idValidation(id);
        return googleCalendarService.createEvent(id);
    }

    private void idValidation(Long id) {
        if (id <= 0) {
            throw new DataValidationException("Id must be greater than 0");
        }
    }
}