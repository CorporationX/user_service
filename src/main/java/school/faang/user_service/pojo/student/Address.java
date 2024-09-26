
package school.faang.user_service.pojo.student;

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
public class Address {

    @NotNull
    @NotBlank
    private String street;

    @NotNull
    @NotBlank
    private String city;

    private String state;

    @NotNull
    @NotBlank
    private String country;

    private String postalCode;
}
