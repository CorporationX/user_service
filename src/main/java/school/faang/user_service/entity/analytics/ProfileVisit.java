package school.faang.user_service.entity.analytics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import school.faang.user_service.entity.user.User;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая факт посещения профиля пользователем.
 * <p>
 * Отражает связь между двумя пользователями:
 * <ul>
 *   <li>{@link #visitor} — пользователь, который зашел на профиль</li>
 *   <li>{@link #visited} — пользователь, чей профиль был посещён</li>
 * </ul>
 * Также хранится дата и время визита ({@link #visitedAt}).
 * </p>
 * <p>
 * Таблица: {@code profile_visits}.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
@Entity
@Table(name = "profile_visits")
@Getter
@Setter
@ToString
public class ProfileVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @Column(name = "visitor_id", insertable = false, updatable = false)
    private Long visitorId;

    @ManyToOne
    @JoinColumn(name = "visitor_id", nullable = false)
    private User visitor;

    @Column(name = "visited_id", insertable = false, updatable = false)
    private Long visitedId;

    @ManyToOne
    @JoinColumn(name = "visited_id", nullable = false)
    private User visited;

    @Column(name = "visited_at")
    private LocalDateTime visitedAt;
}
