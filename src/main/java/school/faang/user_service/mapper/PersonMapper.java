package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.Person;

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
}
