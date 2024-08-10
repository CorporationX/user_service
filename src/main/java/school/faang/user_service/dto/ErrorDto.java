package school.faang.user_service.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ErrorDto {
    private final String message;
    private final UUID id;

    public ErrorDto(String message) {
        this.message = message;
        this.id = UUID.randomUUID();
    }
}
