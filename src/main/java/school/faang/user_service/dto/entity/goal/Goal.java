package school.faang.user_service.dto.entity.goal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import school.faang.user_service.dto.entity.Skill;
import school.faang.user_service.dto.entity.User;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NamedEntityGraph(
        name = "Goal.graph",
        attributeNodes = {
                @NamedAttributeNode("parent"),
                @NamedAttributeNode("mentor"),
                @NamedAttributeNode("invitations"),
                @NamedAttributeNode("users"),
                @NamedAttributeNode("skillsToAchieve")
        }
)
@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_goal_id")
    @ToString.Exclude
    private Goal parent;

    @Column(name = "title", length = 64, nullable = false, unique = true)
    private String title;

    @Column(name = "description", length = 128, nullable = false, unique = true)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private GoalStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deadline")
    private LocalDateTime deadline;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @OneToMany(mappedBy = "goal")
    @ToString.Exclude
    private List<GoalInvitation> invitations;

    @ManyToMany
    @JoinTable(
            name = "user_goal",
            joinColumns = @JoinColumn(name = "goal_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    private Set<User> users = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "goal_skill",
            joinColumns = @JoinColumn(name = "goal_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @ToString.Exclude
    private Set<Skill> skillsToAchieve = new LinkedHashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Goal goal = (Goal) o; // This cast is safe because of the class comparison above
        return getId() != null && Objects.equals(getId(), goal.getId());
    }


    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this)
                .getHibernateLazyInitializer()
                .getPersistentClass().hashCode() : getClass().hashCode();
    }

}
