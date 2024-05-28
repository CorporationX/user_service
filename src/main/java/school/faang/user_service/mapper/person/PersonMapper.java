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
        StringBuilder builder = new StringBuilder();
        if (person != null) {
            if (person.getContactInfo() != null && person.getContactInfo().getAddress() != null) {
                if (person.getContactInfo().getAddress().getState() != null) {
                    builder.append("State - ").append(person.getContactInfo().getAddress().getState()).append(", ");
                }
                if (person.getContactInfo().getAddress().getStreet() != null) {
                    builder.append("Street - ").append(person.getContactInfo().getAddress().getStreet()).append(", ");
                }
                if (person.getContactInfo().getAddress().getPostalCode() != null) {
                    builder.append("Postal code - ").append(person.getContactInfo().getAddress().getPostalCode()).append(", ");
                }
            }
            if (person.getEducation() != null) {
                if (person.getEducation().getFaculty() != null) {
                    builder.append("Faculty - ").append(person.getEducation().getFaculty()).append(", ");
                }
                if (person.getEducation().getYearOfStudy() != null) {
                    builder.append("Year of study - ").append(person.getEducation().getYearOfStudy()).append(", ");
                }
                if (person.getEducation().getMajor() != null) {
                    builder.append("Major - ").append(person.getEducation().getMajor()).append(", ");
                }
                if (person.getEducation().getGpa() != null) {
                    builder.append("GPA - ").append(person.getEducation().getGpa()).append(", ");
                }
                if (person.getStatus() != null) {
                    builder.append("Status - ").append(person.getStatus()).append(", ");
                }
            }
            if (person.getEmployer() != null) {
                builder.append("Employer - ").append(person.getEmployer()).append(", ");
            }
            if (person.getPreviousEducation() != null && !person.getPreviousEducation().isEmpty()) {
                if (person.getPreviousEducation().get(0).getDegree() != null) {
                    builder.append("Degree - ").append(person.getPreviousEducation().get(0).getDegree()).append(", ");
                }
                if (person.getPreviousEducation().get(0).getInstitution() != null) {
                    builder.append("Institution - ").append(person.getPreviousEducation().get(0).getInstitution()).append(", ");
                }
                if (person.getPreviousEducation().get(0).getCompletionYear() != null) {
                    builder.append("Completion year - ").append(person.getPreviousEducation().get(0).getCompletionYear()).append(", ");
                }
            }
            int length = builder.length();
            if (length > 2 && builder.substring(length - 2).equals(", ")) {
                builder.setLength(length - 2);
            }
        }
        return builder.toString();
    }
}
