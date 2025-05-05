package school.faang.user_service.entity.pending;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pending_premium_requests")
public class PendingPremiumRequest {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "premium_response_json")
    private String premiumResponseJson;
}
