package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import io.swagger.v3.oas.models.info.Contact;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.csv.StudentCsvDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = ContactMapper.class)
public interface UserMapper {
    ContactMapper INSTANCE = Mappers.getMapper(ContactMapper.class);

    @Mapping(target = "preference", expression = "java(user.getContactPreference().getPreference().toString())")
    @Mapping(target = "contacts", source = "contacts")
    UserDto toUserDto(User user);

    @Mapping(target = "username", expression = "java(dto.getFirstName() + \" \" + dto.getLastName())")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "experience", constant = "0")
    @Mapping(target = "aboutMe", source = "employer")
    @Mapping(target = "country", ignore = true)
    User toUser(StudentCsvDto dto);

    @Mapping(target = "institution", source = "institution")
    @Mapping(target = "specialization", source = "major")
    @Mapping(target = "educationLevel", source = "degree")
    @Mapping(target = "yearFrom", ignore = true)
    @Mapping(target = "yearTo", ignore = true)
    Education toEducation(StudentCsvDto dto);
}