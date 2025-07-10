package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.exception.EntityNotFoundException;

public interface EducationRepository extends JpaRepository<Education, Long> {

    default Education getByIdOrThrow(long educationId) {
        return findById(educationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Education %d not found", educationId))
        );
    }

}
