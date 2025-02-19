package school.faang.user_service.repository.specification;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

public class EventSpecification {
  public static Specification<Event> hasId(Long id) {
    return (root, query, cb) -> id == null ? cb.conjunction() : cb.equal(root.get("id"), id);
  }

  public static Specification<Event> hasTitle(String title) {
    return (root, query, cb) ->
        title == null || title.isBlank()
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
  }

  public static Specification<Event> hasDescription(String description) {
    return (root, query, cb) ->
        description == null || description.isBlank()
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
  }

  public static Specification<Event> hasLocation(String location) {
    return (root, query, cb) ->
        location == null || location.isBlank()
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
  }

  public static Specification<Event> hasMaxAttendees(Integer maxAttendees) {
    return (root, query, cb) ->
        maxAttendees == null ? cb.conjunction() : cb.equal(root.get("maxAttendees"), maxAttendees);
  }

  public static Specification<Event> hasStartDate(LocalDateTime startDate) {
    return (root, query, cb) ->
        startDate == null
            ? cb.conjunction()
            : cb.greaterThanOrEqualTo(root.get("startDate"), startDate);
  }

  public static Specification<Event> hasEndDate(LocalDateTime endDate) {
    return (root, query, cb) ->
        endDate == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("endDate"), endDate);
  }

  public static Specification<Event> hasEventType(EventType eventType) {
    return (root, query, cb) ->
        eventType == null ? cb.conjunction() : cb.equal(root.get("type"), eventType);
  }

  public static Specification<Event> hasEventStatus(EventStatus eventStatus) {
    return (root, query, cb) ->
        eventStatus == null ? cb.conjunction() : cb.equal(root.get("status"), eventStatus);
  }

  public static Specification<Event> hasOwner(Long ownerId) {
    return (root, query, cb) ->
        ownerId == null ? cb.conjunction() : cb.equal(root.get("owner").get("id"), ownerId);
  }

  public static Specification<Event> hasSkillIds(List<Long> skillIds) {
    return (root, query, cb) -> {
      if (skillIds == null || skillIds.isEmpty()) {
        return cb.conjunction();
      }

      var skillsJoin = root.join("relatedSkills");
      return skillsJoin.get("id").in(skillIds);
    };
  }
}
