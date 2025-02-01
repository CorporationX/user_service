package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonToUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", expression = "java(generateUsername(person.getFirstName(), person.getLastName()))")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(target = "password", ignore = true) // generate in service
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "aboutMe", expression = "java(buildAboutMe(person))")
    @Mapping(target = "country", ignore = true)   // set in service
    @Mapping(source = "city", target = "city")
    @Mapping(target = "experience", expression = "java(person.getYearOfStudy())")
    @Mapping(target = "banned", constant = "false")
    User personToUser(Person person);

    default String generateUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            return "user" + System.currentTimeMillis();
        }
        return (firstName.toLowerCase() + "." + lastName.toLowerCase()).replaceAll("\\s+", "");
    }

    default String buildAboutMe(Person p) {
        StringBuilder sb = new StringBuilder();
        if (p.getFaculty() != null) sb.append("Faculty: ").append(p.getFaculty()).append("\n");
        if (p.getMajor() != null) sb.append("Major: ").append(p.getMajor()).append("\n");
        if (p.getDegree() != null) sb.append("Degree: ").append(p.getDegree()).append("\n");
        if (p.getInstitution() != null) sb.append("Institution: ").append(p.getInstitution()).append("\n");
        if (p.getEmployer() != null) sb.append("Employer: ").append(p.getEmployer()).append("\n");
        if (p.getStatus() != null) sb.append("Status: ").append(p.getStatus()).append("\n");
        if (p.getAdmissionDate() != null) sb.append("Admission Date: ").append(p.getAdmissionDate()).append("\n");
        if (p.getGraduationDate() != null) sb.append("Graduation Date: ").append(p.getGraduationDate()).append("\n");
        if (p.getScholarship() != null && p.getScholarship()) sb.append("Scholarship: Yes").append("\n");
        if (p.getGPA() != null) sb.append("GPA: ").append(p.getGPA()).append("\n");
        return sb.toString().trim();
    }
}