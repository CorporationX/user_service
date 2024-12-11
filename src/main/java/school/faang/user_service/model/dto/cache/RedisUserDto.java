package school.faang.user_service.model.dto.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RedisUserDto {
    private Long userId;
    private String username;
    private String fileId;
    private String smallFileId;
}
