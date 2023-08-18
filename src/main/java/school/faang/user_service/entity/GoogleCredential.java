package school.faang.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "google_credentials")
public class GoogleCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "client_id", length = 128, nullable = false)
    private String clientId;

    @Column(name = "client_email", length = 128, nullable = false)
    private String clientEmail;

    @Column(name = "project_id", length = 32, nullable = false)
    private String projectId;

    @Column(name = "auth_uri", length = 64, nullable = false)
    private String authUri;

    @Column(name = "token_uri", length = 64, nullable = false)
    private String tokenUri;

    @Column(name = "auth_provider_x509_cert_url", length = 64, nullable = false)
    private String authProviderCertUrl;

    @Column(name = "client_secret", length = 64, nullable = false)
    private String clientSecret;

    @Column(name = "redirect_uri", length = 64, nullable = false)
    private String redirectUri;

    @Column(name = "javascript_origin", length = 64, nullable = false)
    private String javascriptOrigin;
}
