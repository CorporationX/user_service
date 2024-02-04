package school.faang.user_service.mapper;

import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserPersonMapper {

    @Autowired
    protected CountryRepository countryRepository;

    @Mapping(target = "username", expression = "java(person.getFirstName() + \" \" + person.getLastName())")
    @Mapping(target = "email", source = "person.contactInfo.email")
    @Mapping(target = "phone", source = "person.contactInfo.phone")
    @Mapping(target = "city", source = "person.contactInfo.address.city")
    @Mapping(target = "country", expression = "java(getCountry(person))")
    @Mapping(target = "aboutMe", expression = "java(getAboutMe(person))")
    public abstract User toUser(Person person);

    protected String createAboutMe(Person person) {
        String state = person.getContactInfo().getAddress().getState();
        String faculty = person.getEducation().getFaculty();
        String major = person.getEducation().getMajor();
        int yearOfStudy = person.getEducation().getYearOfStudy();
        String employer = person.getEmployer();

        return String.format("Student from %s State with Education at the Faculty of %s as a %s. " +
                        "Year of study is %s. " +
                        "Also Employee is %s",
                state, faculty, major, yearOfStudy, employer);
    }

    protected Country getCountry(Person person) {
        String countryTitle = person.getContactInfo().getAddress().getCountry();
        Country country = countryRepository.findByTitle(countryTitle);
        if (country == null) {
            country = Country.builder()
                    .title(countryTitle)
                    .build();
        }
        return country;
    }
}
