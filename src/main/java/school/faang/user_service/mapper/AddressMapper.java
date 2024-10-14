package school.faang.user_service.mapper;

import com.json.student.Address;
import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.AddressDto;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressDto addressDto);
}