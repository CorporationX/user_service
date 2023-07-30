package school.faang.user_service.entity;

import com.google.api.client.auth.oauth2.StoredCredential;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "google_token")
public class GoogleToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "oauth_client_id")
    private String oauthClientId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expiration_time_milliseconds")
    private Long expirationTimeMilliseconds;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public StoredCredential toStoredCredential() {
        return new StoredCredential()
                .setAccessToken(this.accessToken)
                .setRefreshToken(this.refreshToken)
                .setExpirationTimeMilliseconds(this.expirationTimeMilliseconds);
    }
}