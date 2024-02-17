package school.faang.user_service.dto.google;

import com.google.api.client.util.DateTime;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.TimeZone;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCalendarDto {
    private String title;
    private String location;
    private String description;
    private DateTime startDateTime;
    private DateTime endDateTime;
}
