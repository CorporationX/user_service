package school.faang.user_service.dto;

import lombok.Data;

@Data
public class ValidationErrorDTO {
    private String field;
    private String message;

    public ValidationErrorDTO(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
