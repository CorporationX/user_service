package school.faang.user_service.entity.person;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {

    @NotNull
    private String email;

    @NotNull
    private String phone;

    @NotNull
    private Address address;
}
