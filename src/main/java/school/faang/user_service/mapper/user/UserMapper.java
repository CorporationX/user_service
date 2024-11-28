package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.dto.user.feed.UserNewsFeedDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.pojo.student.Person;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "countryId", target = "country.id")
    User toEntity(UserRegistrationDto userRegistrationDto);

    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "userProfilePic.fileId", target = "userProfilePicId")
    UserDto toDto(User user);

    List<UserDto> toDtos(List<User> users);

    List<User> toEntities(List<Person> persons);

    @Mapping(target = "username", expression = "java(person.getFirstName() + \" \" + person.getLastName())")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country.title", source = "contactInfo.address.country")
    @Mapping(target = "aboutMe", expression = "java(mapAboutMeFromPerson(person))")
    User toEntity(Person person);

    default String mapAboutMeFromPerson(Person person) {
        return concatAboutMeInformation(person);
    }

    private String concatAboutMeInformation(Person person) {
        StringBuilder sb = new StringBuilder();

        String state = person.getContactInfo().getAddress().getState();
        if (state != null && !state.isBlank()) {
            sb.append("Address: { State - ")
                    .append(state)
                    .append(" } ");
        }
        if (person.getEducation() != null) {
            String faculty = person.getEducation().getFaculty();

            if (faculty != null && !faculty.isBlank()) {
                sb.append("\nFaculty - ").append(faculty);
            }

            int yearOdStudy = person.getEducation().getYearOfStudy();

            if (yearOdStudy > 0) {
                sb.append("\nYear of study - ").append(yearOdStudy);
            }

            String major = person.getEducation().getMajor();

            if (major != null && !major.isBlank()) {
                sb.append("\nMajor - ").append(major);
            }
        }
        String employer = person.getEmployer();

        if (employer != null && !employer.isBlank()) {
            sb.append("\nEmployer - ").append(employer);
        }

        return sb.toString();
    }

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "followees", target = "folowees", qualifiedByName = "mapFolloweesToIds")
    UserNewsFeedDto toNewsFeedDto(User user);

    List<UserNewsFeedDto> toUserNewsFeedDtos(List<User> users);

    @Named("mapFolloweesToIds")
    default List<Long> mapFolloweesToIds(List<User> followees) {
        if (followees == null) {
            return List.of();
        }
        return followees.stream()
                .map(User::getId)
                .toList();
    }
}
