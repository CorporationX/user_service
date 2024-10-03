package school.faang.user_service.mapper.user;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import school.faang.user_service.pojo.Person;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named("UserMapperUtil")
@Component
@RequiredArgsConstructor
public class UserMapperUtil {

    @Named("getUserName")
    public String getUserName(Person person) {
        return person.getFirstName() + person.getLastName();
    }

    @Named("getAboutMe")
    public String getAboutMe(Person person) {
        return Stream.of(
                        person.getContactInfo().getAddress().getState(),
                        person.getEducation().getFaculty(),
                        person.getEducation().getYearOfStudy(),
                        person.getEducation().getMajor(),
                        person.getEmployer()
                ).filter(Objects::nonNull)
                .map(Objects::toString)
                .collect(Collectors.joining("\n"));
    }
}
