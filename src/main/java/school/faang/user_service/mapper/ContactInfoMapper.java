package school.faang.user_service.mapper;

import com.json.student.ContactInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.ContactInfoDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactInfoMapper {
    ContactInfo toEntity(ContactInfoDto contactInfoDto);
}