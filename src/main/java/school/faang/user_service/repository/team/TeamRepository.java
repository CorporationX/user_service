package school.faang.user_service.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.team.Team;
import school.faang.user_service.exception.EntityNotFoundException;

public interface TeamRepository extends JpaRepository<Team, Long> {
    default Team getByIdOrThrow(long teamId) {
        return findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Team %d not found", teamId)));
    }
}
