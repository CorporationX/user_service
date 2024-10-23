package school.faang.user_service.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProfileViewEventDto {
    private Long viewerId;
    private String viewerUserName;
    private Long viewedProfileId;
}
