package school.faang.user_service.mapper;

import com.json.student.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.utils.PasswordGenerator;

import static java.lang.String.*;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public abstract class PersonToUserMapper {

    @Value("${app.password-length}")
    public int passwordLength;

    @Mapping(source = "person", target = "username", qualifiedByName = "concatenateNames")
    @Mapping(target = "password", expression = "java(setDefaultPassword())")
    @Mapping(target = "active", expression = "java(setActiveAsTrue())")
    @Mapping(source = "person.contactInfo.address.country", target = "country", qualifiedByName = "setCountry")
    @Mapping(source = "person.contactInfo.address.city", target = "city")
    public abstract User mapToUser(Person person);

    @Named("concatenateNames")
    public String concatenateNames(Person person) {
        return join("_", person.getFirstName(), person.getLastName());
    }

    @Named("setDefaultPassword")
    public String setDefaultPassword() {
        return PasswordGenerator.generatePassword(passwordLength);
    }

    @Named("setActiveAsTrue")
    public Boolean setActiveAsTrue() {
        return true;
    }

    @Named("setCountry")
    public Country setCountry(String countryName) {
        if (countryName == null) return null;
        return Country.builder()
                .title(countryName)
                .build();
    }
}
