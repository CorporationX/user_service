package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    @NotNull(message = "The name cannot be empty.")
    private String namePattern;
    @Size(max = 100, message = "The field 'about' can contain no more than 100 characters")
    private String aboutPattern;
    @NotBlank(message = "")
    @Email(message = "")
    private String emailPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String phonePattern;
    private String skillPattern;
    private Integer experienceMin;
    private Integer experienceMax;
    private int page;
    private int pageSize;
}
