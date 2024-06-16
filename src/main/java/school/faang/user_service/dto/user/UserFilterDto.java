package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserFilterDto {

    @Size(max = 64, message = "username pattern should be less than 65 symbols")
    private String namePattern;

    @Size(max = 4096, message = "about pattern should be less than 4097 symbols")
    private String aboutPattern;

    @Size(max = 64, message = "email pattern should be less than 65 symbols")
    private String emailPattern;

    private String contactPattern;

    private String countryPattern;

    @Size(max = 64, message = "city pattern should be less than 65 symbols")
    private String cityPattern;

    @Size(max = 32, message = "phone pattern should be less than 33 symbols")
    private String phonePattern;
    private String skillPattern;
    private Integer experienceMin;
    private Integer experienceMax;
}
