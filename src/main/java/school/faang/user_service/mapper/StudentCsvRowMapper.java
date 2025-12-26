package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.entity.person.Address;
import school.faang.user_service.entity.person.ContactInfo;
import school.faang.user_service.entity.person.Education;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.entity.person.PreviousEducation;
import school.faang.user_service.mapper.csvmapper.StudentCsvRow;

@Mapper(componentModel = "spring")
public interface StudentCsvRowMapper {

    @Mapping(target = "contactInfo", source = ".")
    @Mapping(target = "educations", expression = "java(java.util.Collections.singletonList(toEducation(row)))")
    @Mapping(target = "previousEducation",
            expression = "java(java.util.Collections.singletonList(toPreviousEducation(row)))")
    Person toPerson(StudentCsvRow row);

    @Mapping(target = "address", source = ".")
    ContactInfo toContactInfo(StudentCsvRow row);

    Address toAddress(StudentCsvRow row);

    Education toEducation(StudentCsvRow row);

    PreviousEducation toPreviousEducation(StudentCsvRow row);
}




