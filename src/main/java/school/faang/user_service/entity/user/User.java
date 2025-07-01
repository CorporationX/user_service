package school.faang.user_service.entity.user;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.country.Country;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.score.UserScore;
import school.faang.user_service.entity.skill.Skill;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"country", "followers", "followees", "ownedEvents", "mentees",
        "receivedMentorshipRequests", "sentMentorshipRequests", "sentGoalInvitations",
        "receivedGoalInvitations", "setGoals", "goals", "skills", "participatedEvents",
        "recommendationsGiven", "contacts", "ratings", "contactPreference",
        "premium", "education", "career", "workSchedule"})
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 64, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 64, nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 32, unique = true)
    private String phone;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "about_me", length = 4096)
    private String aboutMe;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(name = "city", length = 64)
    private String city;

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
    private List<User> followers = new ArrayList<>();

    @ManyToMany(mappedBy = "followers")
    private List<User> followees = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Event> ownedEvents = new ArrayList<>();

    @ManyToMany(mappedBy = "mentors")
    private List<User> mentees = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "mentorship",
            joinColumns = @JoinColumn(name = "mentee_id"),
            inverseJoinColumns = @JoinColumn(name = "mentor_id"))
    private List<User> mentors = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private List<MentorshipRequest> receivedMentorshipRequests = new ArrayList<>();

    @OneToMany(mappedBy = "requester")
    private List<MentorshipRequest> sentMentorshipRequests = new ArrayList<>();

    @OneToMany(mappedBy = "inviter")
    private List<GoalInvitation> sentGoalInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "invited")
    private List<GoalInvitation> receivedGoalInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "mentor")
    private List<Goal> setGoals = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    private List<Goal> goals = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    private List<Skill> skills = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> participatedEvents = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<Recommendation> recommendationsGiven = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private List<Recommendation> recommendationsReceived = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Contact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings = new ArrayList<>();

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

    @OneToMany(mappedBy = "user")
    private List<Education> education = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Career> career = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private WorkSchedule workSchedule;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserScore score;

    @OneToMany(mappedBy = "user")
    private List<Promotion> promotions = new ArrayList<>();
}