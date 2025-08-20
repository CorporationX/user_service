package school.faang.user_service.dto.career;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCareerDto {

    @NonNull
    @Past(message = "The variable cannot be created in the future")
    private LocalDate from;

    @NonNull
    @Past(message = "The variable cannot be created in the future")
    private LocalDate to;

    @NonNull
    @NotBlank(message = "The company must have a name")
    private String company;

    @NonNull
    @NotBlank(message = "The position cannot be empty")
    private String position;
}
