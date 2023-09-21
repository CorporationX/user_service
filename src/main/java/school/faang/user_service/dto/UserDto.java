package school.faang.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotBlank
    @Size(min = 1, max = 64)
    private String username;
    @Email
    @NotBlank
    private String email;
    @Size(max = 15)
    private String phone;
    @NotBlank
    @Size(max = 128)
    private String password;
    @Size(max = 4096)
    private String aboutMe;
    @NotNull
    private CountryDto country;
    @Size(max =64)
    private String city;
    @Max(100)
    private Integer experience;
    private PreferredContact preferredContact;
    private List<Long> mentorIds;
}
