package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.ProjectSubscriptionDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.ProjectSubscription;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectSubscriptionMapper {
    @Mapping(target = "followerId", source = "follower.id")
    ProjectSubscriptionDto toDto(ProjectSubscription projectSubscription);
    ProjectSubscription toEntity(ProjectSubscriptionDto projectSubscriptionDto);
    List<ProjectSubscriptionDto> toProjectSubscriptionDto(List<ProjectSubscription> projectSubscriptions);
}
