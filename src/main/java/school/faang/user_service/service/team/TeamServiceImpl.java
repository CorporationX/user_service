package school.faang.user_service.service.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.team.TeamDto;
import school.faang.user_service.entity.team.Team;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.FileUploadException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.TeamMapper;
import school.faang.user_service.repository.team.TeamRepository;
import school.faang.user_service.service.image.ImageService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final ImageService imageService;
    private final UserContext userContext;
    private final TeamMapper teamMapper;

    @Override
    public TeamDto uploadAvatar(long teamId, MultipartFile file) {
        Team team = teamRepository.getByIdOrThrow(teamId);
        checkManagerRights(team);

        try {
            String avatarKey = imageService.uploadTeamAvatar(file, teamId);
            team.setAvatarKey(avatarKey);
            teamRepository.save(team);
            return teamMapper.toTeamDto(team);
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload avatar for team " + teamId, e);
        }
    }

    @Override
    public void deleteAvatar(long teamId) {
        Team team = teamRepository.getByIdOrThrow(teamId);
        checkManagerRights(team);

        if (team.getAvatarKey() != null) {
            imageService.deleteTeamAvatar(team.getAvatarKey());
            team.setAvatarKey(null);
            teamRepository.save(team);
        }
    }

    @Override
    public byte[] getAvatar(long teamId) {
        Team team = teamRepository.getByIdOrThrow(teamId);
        if (team.getAvatarKey() == null) {
            throw new EntityNotFoundException("Team " + teamId + " does not have an avatar");
        }
        return imageService.getTeamAvatar(team.getAvatarKey());
    }

    private void checkManagerRights(Team team) {
        Long currentUserId = userContext.getUserId();
        if (!team.getManager().getId().equals(currentUserId)) {
            throw new ForbiddenException("Only team manager can modify avatar. Current user: " + currentUserId + ", team manager: " + team.getManager().getId());
        }
    }
}