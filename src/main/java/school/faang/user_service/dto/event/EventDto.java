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

  public EventDto(Long id, String title, LocalDateTime startDate, LocalDateTime endDate, Long ownerId, String description, List<SkillDto> relatedSkills, String location, int maxAttendees) {
    this.id = id;
    this.title = title;
    this.startDate = startDate;
    this.endDate = endDate;
    this.ownerId = ownerId;
    this.description = description;
    this.relatedSkills = relatedSkills;
    this.location = location;
    this.maxAttendees = maxAttendees;
  }

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

  public void setTitle(String title) {
    this.title = title;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }
}
