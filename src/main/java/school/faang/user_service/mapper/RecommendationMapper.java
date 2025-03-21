package school.faang.user_service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

import static school.faang.user_service.service.RecommendationService.ID_NULL_EXCEPTION;

@Mapper(componentModel = "spring", uses = SkillOfferMapper.class)
public interface RecommendationMapper {

    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "receiver", source = "receiverId")
    @Mapping(target = "skillOffers", source = "skillOffersDto")
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Recommendation toRecommendation(RecommendationDto recommendationDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOffersDto", source = "skillOffers")
    RecommendationDto toRecommendationDto(Recommendation recommendation);

    List<RecommendationDto> toRecommendationDtoList(List<Recommendation> recommendations);

    default User mapIdToUser(Long id) {
        if (id == null) {
            throw new DataValidationException(ID_NULL_EXCEPTION);
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
