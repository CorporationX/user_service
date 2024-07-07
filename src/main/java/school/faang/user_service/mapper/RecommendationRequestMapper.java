package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(uses = {UserMapper.class, SkillRequestMapper.class})
public interface RecommendationRequestMapper {

    RecommendationRequestMapper INSTANCE = Mappers.getMapper(RecommendationRequestMapper.class);

    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    @Mapping(source = "requesterId", target = "requester")
    @Mapping(source = "receiverId", target = "receiver")
    @Mapping(source = "skills", target = "skills")
    RecommendationRequest dtoToEntity(RecommendationRequestDto recommendationRequestDto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skills", target = "skills")
    RecommendationRequestDto entityToDto(RecommendationRequest recommendationRequest);
}
