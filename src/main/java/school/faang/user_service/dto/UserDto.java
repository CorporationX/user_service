package school.faang.user_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.dto.contact.ContactDto;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String preference;
    private List<ContactDto> contacts;
}
