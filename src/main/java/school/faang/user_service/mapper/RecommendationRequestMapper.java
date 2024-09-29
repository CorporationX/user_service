package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillRequestMapper.class)
public interface RecommendationRequestMapper {

    @Mapping(source = "requesterId", target = "requester", qualifiedByName = "mapToRequester")
    @Mapping(source = "receiverId", target = "receiver", qualifiedByName = "mapToReceiver")
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Named("mapToRequester")
    default User mapToRequester(Long requesterId) {
        return User.builder()
                .id(requesterId)
                .build();
    }

    @Named("mapToReceiver")
    default User mapToReceiver(Long receiverId) {
        return User.builder()
                .id(receiverId)
                .build();
    }


    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationRequestDto toDto(RecommendationRequest entity);

    List<RecommendationRequestDto> toDto(List<RecommendationRequest> entities);
}
