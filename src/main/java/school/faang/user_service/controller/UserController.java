package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.DtoDeactiv;
import school.faang.user_service.service.DeactivatingService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final DeactivatingService deactivatingService;

    @GetMapping("/activation/{userId}")
    public DtoDeactiv deactivating(@PathVariable @Min(0) long userId) {
        return deactivatingService.deactivatingTheUser(userId);
    }
}

