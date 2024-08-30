package school.faang.user_service.mapper.person;

import school.faang.user_service.com.json.student.Person;
import org.mapstruct.*;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
    @Mapping(source = "contactInfo.email", target = "email")
    @Mapping(source = "contactInfo.phone", target = "phone")
    @Mapping(source = "contactInfo.address.city", target = "city")
    @Mapping(source = "contactInfo.address.country", target = "country.title")
    @Mapping(source = "person", target = "username", qualifiedByName = "nameUser")
    User personToUser(Person person);

    @Named("nameUser")
    default String nameUser(Person person) {
        return person.getFirstName() + " " + person.getLastName();
    }
}