package school.faang.user_service.mappers;

import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;


@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillOfferMapper.class)
public interface RecommendationMapper {
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "mapSkillOffersDtoToEntity")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto toDto(Recommendation entity);




}
