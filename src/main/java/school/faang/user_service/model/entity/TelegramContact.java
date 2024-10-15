package school.faang.user_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "telegram_contact")
public class TelegramContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_user_name", nullable = false)
    private String telegramUserName;

    @Column(name = "telegram_user_id")
    private String telegramUserId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
