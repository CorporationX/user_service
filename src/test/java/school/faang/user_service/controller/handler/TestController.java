package school.faang.user_service.controller.handler;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/validate")
    public void validate(@Valid @RequestBody TestDto dto) {}

    @GetMapping("/illegal/argument")
    public void throwIllegalArgument() {
        throw new IllegalArgumentException("Bad input value");
    }

    @GetMapping("/illegal/state")
    public void throwIllegalState() {
        throw new IllegalStateException("Incorrect mentoring request data");
    }

    @GetMapping("/data/validation")
    public void throwDataValidation() {
        throw new DataValidationException("experience min or experience max cannot be less than zero");
    }

    @GetMapping("/entity")
    public void throwEntityNotFound() {
        throw new EntityNotFoundException("It seems this user does not exist");
    }

    @GetMapping("/forbidden")
    public void throwForbidden() {
        throw new ForbiddenException("user is trying to change someone else's data");
    }

    // example of unexpected error: NPE
    @GetMapping("/null")
    public void throwNull() {
        throw new NullPointerException();
    }
}