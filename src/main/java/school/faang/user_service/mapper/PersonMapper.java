package school.faang.user_service.mapper;

import com.json.student.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
    @Mapping(source = "contactInfo.email", target = "email")
    @Mapping(source = "contactInfo.phone", target = "phone")
    @Mapping(source = "contactInfo.address.city", target = "city")
    @Mapping(source = "contactInfo.address.country", target = "country", qualifiedByName = "toCountry")
    User personToUser(Person person);

    @Named("toCountry")
    default Country toCountry(String countryName) {
        return Country.builder()
                .title(countryName)
                .build();
    }
}
