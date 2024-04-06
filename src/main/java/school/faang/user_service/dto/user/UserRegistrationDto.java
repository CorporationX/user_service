package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    private Long id;
    @NotBlank
    private String username;
    @Size(max = 16, min = 6)
    @NotBlank
    private String password;
    @Email
    @NotBlank
    private String email;
    @Pattern(regexp="\\d{10}")
    private String phone;
    @NotBlank
    String country;
    private String profilePicFileId;
    private String profilePicSmallFileId;
}