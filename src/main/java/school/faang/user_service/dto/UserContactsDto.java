package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@Builder
public class UserContactsDto {
    @Positive(message = "Id must be a positive integer")
    @NotNull(message = "Id is required")
    private Long id;

    @Email
    private String email;

    @Size(min = 5, max = 30, message = "Username must be between 5 and 30 characters")
    private String username;


    @Pattern(
            regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$",
            message = "Phone number must be valid and include country code if needed"
    )
    private String phone;

    @NotNull(message = "Preferred contact is required")
    private PreferredContact preference;
}
