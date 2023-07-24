package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.DtoDeactiv;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.service.DeactivatingService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final DeactivatingService deactivatingService;

    @PostMapping("/activation/{userId}")
    public DtoDeactiv deactivating(@PathVariable Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new DataValidException("the field cannot be empty");
        }
        // deactivatingService
        return new DtoDeactiv("");
    }
}

