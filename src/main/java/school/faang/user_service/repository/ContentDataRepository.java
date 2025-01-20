package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.dto.entity.ContentData;

public interface ContentDataRepository extends JpaRepository<ContentData, Long> {
}
