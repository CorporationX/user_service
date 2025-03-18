package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SkillRequestMapper.class})
public interface RecommendationRequestMapper {

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    RecommendationRequest toRecommendationRequest(RecommendationRequestDto recommendationRequestDto);

    @InheritInverseConfiguration
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);

    List<RecommendationRequestDto> toRecommendationRequestDtoList(List<RecommendationRequest> recommendationRequestList);
}
