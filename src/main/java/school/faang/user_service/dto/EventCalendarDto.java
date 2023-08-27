package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.DateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class EventCalendarDto {
    private List<Integer> participantsIds;

    @NotNull
    private Long authorId;

    @NotNull
    private String summary;

    @NotNull
    private String location;

    @NotNull
    private String description;

    private DateTime startDateTime;

    private DateTime endDateTime;

    private TimeZone timeZone;
}
