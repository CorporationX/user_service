package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.PersonDto;
import school.faang.user_service.model.entity.Country;
import school.faang.user_service.model.entity.User;

@Mapper(componentModel = "spring", uses = {ContactInfoMapper.class, AddressMapper.class, EducationMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonToUserMapper {

    @Mapping(target = "username", expression = "java(person.firstName() + \" \" + person.lastName())") // Изменено
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country", source = "contactInfo.address.country")
    @Mapping(target = "aboutMe", expression = "java(mapAboutMe(person))")
    User personToUser(PersonDto person);

    default Country map(String countryName) {
       return new Country(0, countryName, null);
    }

    default String mapAboutMe(PersonDto person) {
        StringBuilder aboutMe = new StringBuilder();
        if (person.contactInfo() != null && person.contactInfo().address() != null
                && person.contactInfo().address().getState() != null) {
            aboutMe.append(person.contactInfo().address().getState()).append(", ");
        }
        if (person.education() != null) {
            aboutMe.append(person.education().faculty())
                    .append(", ")
                    .append(person.education().yearOfStudy())
                    .append(", ")
                    .append(person.education().major());
        }

        if (person.employer() != null) {
            aboutMe.append(", ").append(person.employer());
        }
        return aboutMe.toString();
    }
}