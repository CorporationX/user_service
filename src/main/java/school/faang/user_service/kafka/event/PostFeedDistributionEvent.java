package school.faang.user_service.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostFeedDistributionEvent {

    private List<Long> followerIds;
    private List<EventPostDto> eventPostDtos;
}
