package school.faang.user_service.entity.person;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviousEducation {

    @NotNull
    private String degree;

    @NotNull
    private String institution;

    @NotNull
    private Integer completionYear;
}

