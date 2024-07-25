package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.sql.Update;
import school.faang.user_service.dto.validatedto.Create;

@NoArgsConstructor
@Data
public class UserDto {

    @NotNull(groups = {Update.class})
    @Null(groups = {Create.class})
    private Long id;

    @NotBlank(message = "username should not be blank", groups = {Create.class, Update.class})
    private String username;

    @NotBlank(message = "city should not be blank", groups = {Create.class, Update.class})
    private String city;

    @Email
    @NotBlank(message = "email should not be blank", groups = {Create.class, Update.class})
    private String email;

}
