package school.faang.user_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @PostMapping("deactivate/{userId}")
    public void deactivateProfile() {

    }
}
