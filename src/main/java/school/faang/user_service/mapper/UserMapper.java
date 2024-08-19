package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.*;
import school.faang.user_service.service.user.extractor.SafeExtractor;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "country.title", target = "countryTitle")
    UserDto toDto(User user);

    @Mapping(source = "countryTitle", target = "country.title")
    User toEntity(UserDto userDto);

    @Mapping(target = "username", expression = "java(person.getFirstName() + ' ' + person.getLastName())")
    @Mapping(source = "contactInfo.email", target = "email")
    @Mapping(source = "contactInfo.phone", target = "phone")
    @Mapping(source = "contactInfo.address.city", target = "city")
    @Mapping(target = "aboutMe", expression = "java(generateAboutMe(person))")
    User personToUser(Person person);

    default String generateAboutMe(Person person) {
        StringBuilder aboutMe = new StringBuilder();

        String state = SafeExtractor.extract(person.getContactInfo(), contactInfo -> contactInfo.getAddress().getState());
        if (state != null) {
            aboutMe.append(state).append(", ");
        }

        String faculty = SafeExtractor.extract(person.getEducation(), Education::getFaculty);
        if (faculty != null) {
            aboutMe.append(faculty).append(", ");
        }

        Integer yearOfStudy = SafeExtractor.extract(person.getEducation(), Education::getYearOfStudy);
        if (yearOfStudy != null) {
            aboutMe.append(yearOfStudy).append(", ");
        }

        String major = SafeExtractor.extract(person.getEducation(), Education::getMajor);
        if (major != null) {
            aboutMe.append(major).append(", ");
        }

        String employer = SafeExtractor.extract(person, Person::getEmployer);
        if (employer != null) {
            aboutMe.append(employer);
        }

        return aboutMe.toString();
    }
}
