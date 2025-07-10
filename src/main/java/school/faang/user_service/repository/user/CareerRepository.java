package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.exception.EntityNotFoundException;

public interface CareerRepository extends JpaRepository<Career, Long> {

    default Career getByIdOrThrow(long careerId) {
        return findById(careerId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Career %d not found", careerId))
        );
    }

}
