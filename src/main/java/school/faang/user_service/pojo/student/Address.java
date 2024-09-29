
package school.faang.user_service.pojo.student;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("street")
    private String street;

    @NotNull
    @NotBlank
    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @NotNull
    @NotBlank
    @JsonProperty("country")
    private String country;

    @JsonProperty("postalCode")
    private String postalCode;
}
