package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipAcceptedEvent {
    private long idRequestAuthor;
    private long idRequestRecipient;
    private long idRequest;
}
