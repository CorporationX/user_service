package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.PersonDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", uses = {ContactInfoMapper.class, AddressMapper.class, EducationMapper.class})
public interface PersonToUserMapper {

    @Mapping(target = "username", expression = "java(person.getFirstName() + \" \" + person.getLastName())")
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
        if (person.getContactInfo() != null && person.getContactInfo().getAddress() != null
                && person.getContactInfo().getAddress().getState() != null) {
            aboutMe.append(person.getContactInfo().getAddress().getState()).append(", ");
        }
        if (person.getEducation() != null) {
            aboutMe.append(person.getEducation().getFaculty())
                    .append(", ")
                    .append(person.getEducation().getYearOfStudy())
                    .append(", ")
                    .append(person.getEducation().getMajor());
        }

        if (person.getEmployer() != null) {
            aboutMe.append(", ").append(person.getEmployer());
        }
        return aboutMe.toString();
    }
}