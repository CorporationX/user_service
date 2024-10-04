package school.faang.user_service.mapper;

import com.json.student.ContactInfo;
import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.ContactInfoDto;

@Mapper(componentModel = "spring")
public interface ContactInfoMapper {
    ContactInfo toEntity(ContactInfoDto contactInfoDto);
}