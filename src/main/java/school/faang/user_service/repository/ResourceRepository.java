package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.resource.Resource;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("""
            SELECT r FROM Resource AS r
            JOIN User AS u ON r.key = u.userProfilePic.fileId OR r.key = u.userProfilePic.smallFileId
            WHERE u.id = :userId""")
    List<Resource> findAllByUserId(long userId);
}
