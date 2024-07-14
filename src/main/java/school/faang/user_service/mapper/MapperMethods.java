package school.faang.user_service.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MapperMethods {
    private static final String SKILL_REQUEST_NOT_IN_DB = "SkillRequest is not in database";
    private final SkillRequestRepository skillRequestRepository;

    @Named("getSkillRequests")
    public List<SkillRequest> skillRequestIdsToSkillRequest(List<Long> skillsIds) {
        return skillsIds.stream()
                .map(skillsId -> skillRequestRepository
                        .findById(skillsId)
                        .orElseThrow(() -> new RuntimeException(SKILL_REQUEST_NOT_IN_DB)))
                .toList();
    }
}
