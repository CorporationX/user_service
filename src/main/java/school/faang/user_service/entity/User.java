package school.faang.user_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.event.Rating;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(max = 64, message = "Имя пользователя не должно превышать 64 символа")
    @Column(name = "username", length = 64, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 64, nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]*$", message = "Телефон должен содержать только цифры и начинаться с '+'")
    @Size(max = 32, message = "Телефон не должен превышать 32 символа")
    @Column(name = "phone", length = 32, unique = true)
    private String phone;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 128, message = "Пароль должен содержать от 6 до 128 символов")
    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Size(max = 4096, message = "О себе не должно превышать 4096 символов")
    @Column(name = "about_me", length = 4096)
    private String aboutMe;

    @NotNull(message = "Страна не может быть пустой")
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Size(max = 64, message = "Город не должен превышать 64 символа")
    @Column(name = "city", length = 64)
    private String city;

    @Min(value = 0, message = "Опыт не может быть отрицательным")
    @Column(name = "experience")
    private Integer experience;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(name = "subscription",
            joinColumns = @JoinColumn(name = "followee_id"), inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private List<User> followers;

    @ManyToMany(mappedBy = "followers")
    private List<User> followees;

    @OneToMany(mappedBy = "owner")
    private List<Event> ownedEvents;

    @ManyToMany(mappedBy = "mentors")
    private List<User> mentees;

    @ManyToMany
    @JoinTable(name = "mentorship",
            joinColumns = @JoinColumn(name = "mentee_id"),
            inverseJoinColumns = @JoinColumn(name = "mentor_id"))
    private List<User> mentors;

    @OneToMany(mappedBy = "receiver")
    private List<MentorshipRequest> receivedMentorshipRequests;

    @OneToMany(mappedBy = "requester")
    private List<MentorshipRequest> sentMentorshipRequests;

    @OneToMany(mappedBy = "inviter")
    private List<GoalInvitation> sentGoalInvitations;

    @OneToMany(mappedBy = "invited")
    private List<GoalInvitation> receivedGoalInvitations;

    @OneToMany(mappedBy = "mentor")
    private List<Goal> setGoals;

    @ManyToMany(mappedBy = "users")
    private List<Goal> goals;

    @ManyToMany(mappedBy = "users")
    private List<Skill> skills;

    @ManyToMany
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> participatedEvents;

    @OneToMany(mappedBy = "author")
    private List<Recommendation> recommendationsGiven;

    @OneToMany(mappedBy = "receiver")
    private List<Recommendation> recommendationsReceived;

    @OneToMany(mappedBy = "user")
    private List<Contact> contacts;

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fileId", column = @Column(name = "profile_pic_file_id")),
            @AttributeOverride(name = "smallFileId", column = @Column(name = "profile_pic_small_file_id"))
    })
    private UserProfilePic userProfilePic;

    @OneToOne(mappedBy = "user")
    private ContactPreference contactPreference;

    @OneToOne(mappedBy = "user")
    private Premium premium;
}