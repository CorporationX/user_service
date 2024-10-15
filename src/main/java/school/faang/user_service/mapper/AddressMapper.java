package school.faang.user_service.mapper;

import com.json.student.Address;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.AddressDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {
    Address toEntity(AddressDto addressDto);
}