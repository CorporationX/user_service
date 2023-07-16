package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
  private Long id;
  private String title;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Long ownerId;
  private String description;
  private List<Long> relatedSkills;
  private String location;
  private int maxAttendees;
}
