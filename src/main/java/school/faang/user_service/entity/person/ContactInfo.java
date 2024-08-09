package school.faang.user_service.entity.person;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfo {
    private String email;
    private String phone;
    @JsonUnwrapped
    private Address address;
}

