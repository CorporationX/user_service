package school.faang.user_service.mapper;

import com.json.student.Person;
import com.json.student.PreviousEducation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.entity.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @Mapping(target = "username", expression = "java(person.getFirstName() + person.getLastName())")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "aboutMe", expression = "java(person.getFirstName() + person.getLastName())")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country.title", source = "contactInfo.address.country")
    User toEntity(Person person);

    @Mapping(target = "contactInfo.address.street", source = "street")
    @Mapping(target = "contactInfo.address.city", source = "city")
    @Mapping(target = "contactInfo.address.state", source = "state")
    @Mapping(target = "contactInfo.address.country", source = "country")
    @Mapping(target = "contactInfo.address.postalCode", source = "postalCode")
    @Mapping(target = "contactInfo.email", source = "email")
    @Mapping(target = "contactInfo.phone", source = "phone")
    @Mapping(target = "education.faculty", source = "faculty")
    @Mapping(target = "education.yearOfStudy", source = "yearOfStudy")
    @Mapping(target = "education.major", source = "major")
    @Mapping(target = "education.gpa", source = "GPA")
    @Mapping(target = "previousEducation", source = "previousEducation", qualifiedByName = "stringToPreviousEducationList")
    @Mapping(target = "additionalProperties", source = "additionalProperties", qualifiedByName = "stringToMap")
    Person toPerson(Map<String, String> csvRow);

    @Named("stringToPreviousEducationList")
    default List<PreviousEducation> stringToPreviousEducationList(String previousEducation) {
        return Arrays.stream(previousEducation.split(";"))
                .map(this::mapStringToPreviousEducation)
                .collect(Collectors.toList());
    }

    @Named("stringToMap")
    default Map<String, Object> stringToMap(String additionalProperties) {
        Map<String, Object> map = new HashMap<>();
        String[] entries = additionalProperties.split(",");
        for (String entry : entries) {
            String[] keyValue = entry.split(":");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }

    private PreviousEducation mapStringToPreviousEducation(String educationString) {
        String[] parts = educationString.split(",");
        PreviousEducation previousEducation = new PreviousEducation();
        previousEducation.setDegree(parts[0]);
        previousEducation.setInstitution(parts[1]);
        previousEducation.setCompletionYear(Integer.parseInt(parts[2]));
        return previousEducation;
    }
}
