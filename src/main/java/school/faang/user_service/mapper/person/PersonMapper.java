package school.faang.user_service.mapper.person;

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

    @Mapping(target = "username", expression = "java(person.getFirstName() + \"_\" + person.getLastName())")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "aboutMe", source = "person", qualifiedByName = "getAboutMe")
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

    default PreviousEducation mapStringToPreviousEducation(String educationString) {
        String[] parts = educationString.split(",");
        PreviousEducation previousEducation = new PreviousEducation();
        previousEducation.setDegree(parts[0]);
        previousEducation.setInstitution(parts[1]);
        previousEducation.setCompletionYear(Integer.parseInt(parts[2]));
        return previousEducation;
    }

    @Named("getAboutMe")
    default String getAboutMe(Person person) {
        if (person == null) return "";

        StringBuilder builder = new StringBuilder();
        if (person.getContactInfo() != null && person.getContactInfo().getAddress() != null) {
            appendIfNotNull(builder, "State", person.getContactInfo().getAddress().getState());
            appendIfNotNull(builder, "Street", person.getContactInfo().getAddress().getStreet());
            appendIfNotNull(builder, "Postal code", person.getContactInfo().getAddress().getPostalCode());
        }
        if (person.getEducation() != null) {
            appendIfNotNull(builder, "Faculty", person.getEducation().getFaculty());
            appendIfNotNull(builder, "Year of study", person.getEducation().getYearOfStudy());
            appendIfNotNull(builder, "Major", person.getEducation().getMajor());
            appendIfNotNull(builder, "GPA", person.getEducation().getGpa());
        }
        appendIfNotNull(builder, "Status", person.getStatus());
        appendIfNotNull(builder, "Employer", person.getEmployer());

        if (person.getPreviousEducation() != null && !person.getPreviousEducation().isEmpty()) {
            PreviousEducation prevEdu = person.getPreviousEducation().get(0);
            appendIfNotNull(builder, "Degree", prevEdu.getDegree());
            appendIfNotNull(builder, "Institution", prevEdu.getInstitution());
            appendIfNotNull(builder, "Completion year", prevEdu.getCompletionYear());
        }

        if (builder.length() > 2) {
            builder.setLength(builder.length() - 2);
        }
        return builder.toString();
    }

    private void appendIfNotNull(StringBuilder builder, String label, Object value) {
        if (value != null) {
            builder.append(label).append(" - ").append(value).append(", ");
        }
    }
}
