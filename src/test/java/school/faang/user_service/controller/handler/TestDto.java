package school.faang.user_service.controller.handler;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

record TestDto(
        @NotBlank String name,
        @NotNull String aboutMe
) {}

