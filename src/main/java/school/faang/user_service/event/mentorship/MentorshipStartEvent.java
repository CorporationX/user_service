package school.faang.user_service.event.mentorship;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import school.faang.user_service.event.Event;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class MentorshipStartEvent implements Event {
    private long mentorId;
    private long menteeId;
}