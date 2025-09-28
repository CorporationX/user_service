package school.faang.user_service.service.team;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.team.TeamDto;

public interface TeamService {
    TeamDto uploadAvatar(long teamId, MultipartFile file);
    void deleteAvatar(long teamId);
    byte[] getAvatar(long teamId);
}
