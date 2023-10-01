package school.faang.user_service.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    private long id;
    private User user;
    private String contact;
    private ContactType type;
}
