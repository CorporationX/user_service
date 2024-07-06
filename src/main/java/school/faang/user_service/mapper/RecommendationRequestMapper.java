package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId", qualifiedByName = "applyIdFromEntity")
    @Mapping(source = "receiver.id", target = "receiverId", qualifiedByName = "applyIdFromEntity")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(source = "requesterId", target = "requester.id", qualifiedByName = "applyEntityFromId")
    @Mapping(source = "receiverId", target = "receiver.id", qualifiedByName = "applyEntityFromId")
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    private List<Long> convertingSkillsIntoId(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }

    private List<Skill> convertingIdsIntoSkills(List<Long> skillsId) {
        List<Skill> skills = new ArrayList<>();
        for (long id: skillsId) {

        }
    }
}
