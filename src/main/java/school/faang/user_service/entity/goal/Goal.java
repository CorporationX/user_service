package school.faang.user_service.entity.goal;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "goal")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_goal_id")
    private Goal parent;

    @Column(name = "title", length = 64, nullable = false, unique = true)
    private String title;

    @Column(name = "description", length = 128, nullable = false, unique = true)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private GoalStatus status;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @ToString.Exclude
    @OneToMany(mappedBy = "goal")
    private List<GoalInvitation> invitations;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "user_goal",
            joinColumns = @JoinColumn(name = "goal_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "goal_skill",
            joinColumns = @JoinColumn(name = "goal_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skillsToAchieve;
}