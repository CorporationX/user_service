package school.faang.user_service.service.user;

import org.springframework.core.io.Resource;

public interface AvatarGenerator {

    Resource generateByCode(long key);
}
