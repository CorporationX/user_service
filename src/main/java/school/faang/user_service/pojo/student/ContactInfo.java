package school.faang.user_service.pojo.student;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {
    public String email;
    public String phone;
    @JsonUnwrapped
    public Address address;
}
