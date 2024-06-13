package school.faang.user_service.repository.jira;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.jira.JiraAccount;

@Repository
public interface JiraAccountRepository extends JpaRepository<JiraAccount, Long> {
}
