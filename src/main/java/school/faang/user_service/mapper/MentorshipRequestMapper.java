package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requester.id", target = "userRequesterId")
    @Mapping(source = "receiver.id", target = "userReceiverId")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(source = "userRequesterId", target = "requester", qualifiedByName = "userCreator")
    @Mapping(source = "userReceiverId", target = "receiver", qualifiedByName = "userCreator")
    @Mapping(source = "status", target = "status", qualifiedByName = "map")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Named("userCreator")
    default User creator(Long id){
        User user = new User();
        user.setId(id);
        return user;
    }

    @Named("map")
    default RequestStatus convert(String str) {
        return RequestStatus.valueOf(str);
    }

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    List<MentorshipRequestDto> toDto(List<MentorshipRequest> requests);

}
