package school.faang.user_service.dto.filter;

import lombok.Data;

@Data
public class UserFilterDto {
    private String name;
    private String email;
    private String city;
    private String phone;
}