package school.faang.user_service.message.event.reindex.user;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseDocument {
    @Id
    private Long resourceId;
}
