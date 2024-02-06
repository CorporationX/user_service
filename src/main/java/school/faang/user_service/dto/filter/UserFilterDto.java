package school.faang.user_service.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFilterDto extends FilterDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private Boolean active;
    private String aboutMe;
    private Long countryId;
    private String city;

}
