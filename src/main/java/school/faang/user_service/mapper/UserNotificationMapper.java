package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import school.faang.user_service.dto.UserNotificationDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = ContactMapper.class)
public interface UserNotificationMapper {
    ContactMapper INSTANCE = Mappers.getMapper(ContactMapper.class);

    @Mapping(target = "preference", expression = "java(user.getContactPreference().getPreference().toString())")
    @Mapping(target = "contacts", source = "contacts")
    UserNotificationDto toUserNotificationDto(User user);
}
