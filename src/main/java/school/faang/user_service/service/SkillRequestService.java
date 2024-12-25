package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

@Service
@RequiredArgsConstructor
public class SkillRequestService {
    private final SkillRequestRepository repository;

    public SkillRequest save(SkillRequest skillRequest) {
        return repository.save(skillRequest);
    }
}
