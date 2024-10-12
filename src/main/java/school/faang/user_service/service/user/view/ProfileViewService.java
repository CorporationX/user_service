package school.faang.user_service.service.user.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.redis.user.RedisProfileViewEventPublisher;
import school.faang.user_service.dto.user.ProfileViewEventDto;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileViewService {
    private final RedisProfileViewEventPublisher redisProfileViewEventPublisher;
    private final List<ProfileViewEventDto> profileViewEventDtos = new CopyOnWriteArrayList<>();

    @Async("profileViewServicePool")
    public void addToPublish(long receiverId, long actorId) {
        profileViewEventDtos.add(new ProfileViewEventDto(receiverId, actorId));
    }

    @Async("profileViewServicePool")
    public void addToPublish(long receiverId, List<User> actors) {
        actors.forEach(actor ->
                profileViewEventDtos.add(new ProfileViewEventDto(receiverId, actor.getId())));
    }

    public boolean profileViewEventDtosIsEmpty() {
        return profileViewEventDtos.isEmpty();
    }

    public void publishAllProfileViewEvents() {
        List<ProfileViewEventDto> profileViewEventDtosCopy = new ArrayList<>();
        try {
            synchronized (profileViewEventDtos) {
                log.info("Publish profile view events, size: {}", profileViewEventDtos.size());
                profileViewEventDtosCopy = new ArrayList<>(profileViewEventDtos);
                profileViewEventDtos.clear();
            }
            redisProfileViewEventPublisher.publish(profileViewEventDtosCopy);
        } catch (Exception e) {
            log.error("Profile view events publish failed:", e);
            synchronized (profileViewEventDtos) {
                log.info("Save back to main profile view events copy remainder, size: {}",
                        profileViewEventDtosCopy.size());
                profileViewEventDtos.addAll(profileViewEventDtosCopy);
            }
        }
    }
}
