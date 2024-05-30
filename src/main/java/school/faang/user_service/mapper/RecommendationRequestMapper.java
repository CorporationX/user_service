package school.faang.user_service.mapper;

import java.util.List;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.skill.SkillRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillRequestMapper.class)
public interface RecommendationRequestMapper {
    RecommendationRequest fromDto(RecommendationRequestDto recommendationRequestDto);
    
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "requester.id", target = "receiverId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);
}
