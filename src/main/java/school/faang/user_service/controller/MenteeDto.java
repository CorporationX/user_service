package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MenteeDto {
    private String username;
    private String email;
    private String phone;
    private long id;
}
