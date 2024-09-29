package school.faang.user_service.event.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeatFeedEvent {
    private long id;
    private List<Long> folowees;
}
