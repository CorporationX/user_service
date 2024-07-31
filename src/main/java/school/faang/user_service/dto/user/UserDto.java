package school.faang.user_service.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.premium.Premium;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    private String city;
    private Integer experience;
    private String username;
    private Premium premium;
    private String email;
    private String picId;
    private String smallPicId;
    private String phone;
    private String preferenceContact;

    public UserDto(long id) {
        this.id = id;
    }
}
