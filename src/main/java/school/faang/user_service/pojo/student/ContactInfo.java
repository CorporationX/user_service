
package school.faang.user_service.pojo.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {

    @NotNull
    @NotBlank
    @Email
    @JsonProperty("email")
    private String email;

    @NotNull
    @NotBlank
    @JsonProperty("phone")
    private String phone;

    @JsonUnwrapped
    private Address address;
}
