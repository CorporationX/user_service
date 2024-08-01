package school.faang.user_service.mapper.person;

import com.json.student.Person;
import jdk.jfr.Name;
import org.mapstruct.*;
import org.springframework.data.mapping.context.MappingContext;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
    @Mapping(source = "contactInfo.email", target = "email")
    @Mapping(source = "contactInfo.phone", target = "phone")
    @Mapping(source = "contactInfo.address.city", target = "city")
    @Mapping(source = "contactInfo.address.country", target = "country", qualifiedByName = "toCountry")
    @Mapping(source = "firstName", target = "username")
    User personToUser(Person person);

    @Named("toCountry")
    default Country toCountry(String countryName) {
        return Country.builder()
                .title(countryName)
                .build();
    }

//    @Name("toName")
//    default String toName(String firstName, String secondName) {
//        return firstName + " " + secondName;
//    }

//    @Named("toName")
//    default String toName(String firstName, @Context MappingContext context) {
//        String secondName = context.getSource(String.class); // Получаем значение secondName из контекста
//        return firstName + " " + secondName;
//    }
}