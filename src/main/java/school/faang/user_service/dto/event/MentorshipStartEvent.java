package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotNull;

public class MentorshipStartEvent {

    @NotNull
    private long mentor_id;
    @NotNull
    private long mentee_id;

}
