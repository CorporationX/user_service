package school.faang.service.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.service.user.entity.ContentData;

@Repository
public interface ContentDataRepository extends JpaRepository<ContentData, Long> {
}
