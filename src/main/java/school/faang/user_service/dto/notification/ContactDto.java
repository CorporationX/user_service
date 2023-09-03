package school.faang.user_service.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.ContactType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDto {
    private String contact;
    private ContactType type;
}
