package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationFilterDto {
    @Pattern(regexp = "^\\S.*$", message = "inviterNamePattern must not be empty, but can be null")
    private String inviterNamePattern;

    @Pattern(regexp = "^\\S.*$", message = "invitedNamePattern must not be empty, but can be null")
    private String invitedNamePattern;

    @Positive(message = "inviterId must be a positive number or null")
    private Long inviterId;

    @Positive(message = "inviterId must be a positive number or null")
    private Long invitedId;

    private RequestStatus status;
}