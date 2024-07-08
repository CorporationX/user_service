package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class SkillRequestService {
    private final SkillRequestRepository skillRequestRepository;

    public List<Long> findExistingSkillIds(List<Long> skillIds) {
        List<Long> existingSkillIds = skillRequestRepository.findAllById(skillIds).stream()
                .map(SkillRequest::getId)
                .toList();

        if (existingSkillIds.isEmpty()) {
            throw new DataValidationException("Ни один из навыков не существует в базе данных");
        }

        if (existingSkillIds.size() != skillIds.size()) {
            throw new DataValidationException("Некоторые навыки не существуют в базе данных");
        }

        return existingSkillIds;
    }
}
