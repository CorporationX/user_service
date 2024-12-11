package school.faang.user_service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skill")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", length = 64, nullable = false, unique = true)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "user_skill",
            joinColumns = @JoinColumn(name = "skill_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSkillGuarantee> guarantees;

    @ManyToMany(mappedBy = "relatedSkills")
    private List<Event> events;


    @ManyToMany(mappedBy = "skillsToAchieve")
    private List<Goal> goals;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return String.format(("Skill(id=%d, title=%s)"), id, title);
    }

    public void addGuarantee(UserSkillGuarantee guarantee) {
        guarantees.add(guarantee);
    }

    public void removeSameGuarantee(UserSkillGuarantee guarantee) {
        guarantees.removeIf(currGuarantee -> currGuarantee.equals(guarantee));
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public boolean userIsGuarantor(User user) {
        if (user == null) {
            return false;
        }
        return guarantees.stream()
                .anyMatch(guarantee -> guarantee.isGuarantor(user));
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    public void removeGoal(Goal goal) {
        goals.remove(goal);
    }
}