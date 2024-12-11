package school.faang.user_service.entity.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", length = 64, nullable = false)
    private String title;

    @Column(name = "description", length = 4096, nullable = false)
    private String description;

    @Column(name = "start_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate;

    @Column(name = "end_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;

    @Column(name = "location", length = 128)
    private String location;

    @Column(name = "max_attendees")
    private int maxAttendees;

    @ManyToMany(mappedBy = "participatedEvents")
    private List<User> attendees;

    @OneToMany(mappedBy = "event")
    private List<Rating> ratings;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToMany
    @JoinTable(name = "event_skill",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> relatedSkills;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private EventType type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private EventStatus status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isSameOwnerById(Long id){
        if(id == null){
            return false;
        }
        return owner.getId().equals(id);
    }
    public boolean isSameTitle(String sameTitle){
        if(sameTitle.isBlank()){
            return false;
        }
        return title.equals(sameTitle);
    }

    public boolean isSameLocation(String sameLocation){
        if (sameLocation.isBlank()){
            return false;
        }
        return location.equals(sameLocation);
    }

    public String toLogString(){
        return String.format("Event(id=%d, title=%s, owner=%s)", id, title, owner.getId());
    }

    public void removeAttendeeFromEvent(User user) {
        if (user == null) {
            return;
        }
        attendees.remove(user);
    }
}