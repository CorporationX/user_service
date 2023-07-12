package school.faang.user_service.dto.event;

import java.time.LocalDateTime;
import java.util.List;

public class EventDto {
  Long id;
  String title;
  LocalDateTime startDate;
  LocalDateTime endDate;
  Long ownerId;
  String description;
  List<SkillDto> relatedSkills;
  String location;
  int maxAttendees;

  public String getTitle() {
    return title;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public List<SkillDto> getRelatedSkills() {
    return relatedSkills;
  }

  public Long getOwnerId() {
    return ownerId;
  }
}
