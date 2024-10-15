package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.model.dto.RecommendationDto;
import school.faang.user_service.model.entity.Recommendation;

import java.util.List;

@Mapper(componentModel = "spring", uses = SkillOfferMapper.class)
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers")
    RecommendationDto toDto(Recommendation recommendation);

    List<RecommendationDto> toDtoList(List<Recommendation> recommendations);
}

