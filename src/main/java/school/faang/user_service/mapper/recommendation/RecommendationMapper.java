package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.mapper.skill.SkillOfferMapper;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = SkillOfferMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto toDto(Recommendation recommendation);

    List<RecommendationDto> toDtos(List<Recommendation> recommendations);
}
