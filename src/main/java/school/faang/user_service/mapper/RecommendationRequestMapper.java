package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    RecommendationRequestDto toDto(RecommendationRequest request);

    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    List<RecommendationRequestDto> toDto(List<RecommendationRequest> request);

}
