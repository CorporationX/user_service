package school.faang.user_service.dto.event;

public class SkillDto {
  private long id;
  private String title;

  public SkillDto(long id, String title) {
    this.id = id;
    this.title = title;
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }
}
