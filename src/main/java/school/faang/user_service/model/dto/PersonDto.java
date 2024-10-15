package school.faang.user_service.model.dto;

public record PersonDto(String firstName, String lastName, ContactInfoDto contactInfo, EducationDto education,
                        String employer, int yearOfBirth, String group, String studentID) {
}