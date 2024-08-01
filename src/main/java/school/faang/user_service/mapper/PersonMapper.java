package school.faang.user_service.mapper;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {

    @Mapping(source = "person", qualifiedByName = "nameUser", target = "username")
    @Mapping(source = "person", qualifiedByName = "aboutUser", target = "aboutMe")
    @Mapping(source = "person.contactInfo.email", target = "email")
    @Mapping(source = "person.contactInfo.phone", target = "phone")
    @Mapping(source = "person.contactInfo.address.country", target = "country.title")
    @Mapping(source = "person.contactInfo.address.city", target = "city")
    User toUser(Person person);

    @Named("nameUser")
    default String nameUser(Person person) {
        return person.getFirstName() + person.getLastName();
    }

    @Named("aboutUser")
    default String aboutMe(Person person) {
        return "I graduated from " + person.getPreviousEducation().get(0).getInstitution() +
                " in " + person.getPreviousEducation().get(0).getCompletionYear() +
                " with degree " + person.getPreviousEducation().get(0).getDegree() +
                ". I work for an employer " + person.getEmployer();
    }

//    String username;
//    String email;
//    String phone;
//    String password;
//    boolean active;
//    String aboutMe;
//    Country country;
//    String city;
//    Integer experience;
//    LocalDateTime createdAt;
//    LocalDateTime updatedAt;
//    List<User> followers;
//    List<User> followees;
//    private List<Event> ownedEvents;
//    private List<User> mentees;
//    private List<User> mentors;
//    @OneToMany(mappedBy = "receiver")
//    private List<MentorshipRequest> receivedMentorshipRequests;
//    @OneToMany(mappedBy = "requester")
//    private List<MentorshipRequest> sentMentorshipRequests;
//    @OneToMany(mappedBy = "inviter")
//    private List<GoalInvitation> sentGoalInvitations;
//    @OneToMany(mappedBy = "invited")
//    private List<GoalInvitation> receivedGoalInvitations;
//    @OneToMany(mappedBy = "mentor")
//    private List<Goal> setGoals;
//    @ManyToMany(mappedBy = "users")
//    private List<Goal> goals;
//    @ManyToMany(mappedBy = "users")
//    private List<Skill> skills;
//    @ManyToMany
//    @JoinTable(name = "user_event",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "event_id"))
//    private List<Event> participatedEvents;
//    @OneToMany(mappedBy = "author")
//    private List<Recommendation> recommendationsGiven;
//    @OneToMany(mappedBy = "receiver")
//    private List<Recommendation> recommendationsReceived;
//    @OneToMany(mappedBy = "user")
//    private List<Contact> contacts;
//    @OneToMany(mappedBy = "user")
//    private List<Rating> ratings;
//    @Embedded
//    @AttributeOverrides({@AttributeOverride(name = "fileId", column = @Column(name = "profile_pic_file_id")),
//            @AttributeOverride(name = "smallFileId", column = @Column(name = "profile_pic_small_file_id"))})
//    private UserProfilePic userProfilePic;
//    @OneToOne(mappedBy = "user")
//    private ContactPreference contactPreference;
//    @OneToOne(mappedBy = "user")
//    private Premium premium;
}
