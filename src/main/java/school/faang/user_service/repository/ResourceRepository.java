package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.resource.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
