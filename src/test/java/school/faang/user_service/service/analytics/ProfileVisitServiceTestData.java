package school.faang.user_service.service.analytics;

import school.faang.user_service.dto.analytics.ProfileVisitCreateDto;
import school.faang.user_service.entity.analytics.ProfileVisit;
import school.faang.user_service.entity.user.User;

public class ProfileVisitServiceTestData {
    public static User buildUser(long id) {
        return User.builder()
                .id(id)
                .build();
    }

    public static ProfileVisit toEntity(ProfileVisitCreateDto dto) {
        var entity = new ProfileVisit();
        entity.setVisitedId(dto.visitedId());
        entity.setVisitorId(dto.visitorId());
        entity.setVisitedAt(dto.visitedAt());
        return entity;
    }
}
