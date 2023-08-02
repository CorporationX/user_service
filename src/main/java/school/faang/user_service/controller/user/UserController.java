package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.google.api.services.calendar.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.google.GoogleEventService;
import school.faang.user_service.service.user.UserService;

@Tag(name = "Управление пользователями")
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final GoogleEventService googleEventService;

    @Operation(summary = "Деактивировать пользователя по идентификатору")
    @PostMapping("/users/deactivate/{id}")
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }

    @PostMapping("/users/event")
    public void createEvent(@RequestBody Event body) {
        System.out.println(body.getDescription());
//        googleEventService.createEvent(
//            "serhii.rubets@gmail.com",
//            body.getSummary(),
//            body.getDescription(),
//            body.getLocation(),
//            new Date(2023, 8, 2),
//            new Date(2023, 8, 3)
//        );
    }
}
