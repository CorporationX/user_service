package school.faang.user_service.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String username;
    private String password;
}