package school.faang.user_service.repository.adapter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Education;
import school.faang.user_service.repository.EducationRepository;

@Component
@RequiredArgsConstructor
public class EducationRepositoryAdapter {

    private final EducationRepository educationRepository;

    public Education getById(long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Education not found with id:" + educationId));
    }

    public Education save(Education education) {
        return educationRepository.save(education);
    }
}
