package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {
    RecommendationRequest fromDto(RecommendationRequestDto recommendationRequestDto);

    @Mapping(source = "requester",target = "requesterId",qualifiedByName = "requesterToId")
    @Mapping(source = "requester",target = "receiverId",qualifiedByName = "receiverToId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Named("requesterToId")
    static Long requesterToId(User requester) {
        return requester.getId();
    }

    @Named("receiverToId")
    static Long receiverToId(User receiver) {
        return receiver.getId();
    }
}
