
package school.faang.user_service.pojo.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviousEducation {

    @NotNull
    @NotBlank
    private String degree;

    @NotNull
    @NotBlank
    private String institution;

    private int completionYear;
}
