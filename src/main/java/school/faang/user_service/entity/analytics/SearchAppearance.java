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
 * Сущность, описывающая факт появления пользователя в поиске другого пользователя.
 * <p>
 * Отражает связь между:
 * <ul>
 *     <li>{@link #searcher} — пользователем, который выполнил поиск,</li>
 *     <li>{@link #searched} — пользователем, который появился в результатах поиска,</li>
 *     <li>{@link #searchedAt} — временем, когда произошло событие.</li>
 * </ul>
 * </p>
 * <p>
 * Используется для аналитики: позволяет отслеживать, кто и когда находил
 * профиль конкретного пользователя через поиск.
 * </p>
 * <p>
 * Таблица в БД: {@code search_appearance}.
 * </p>
 *
 * @author Myrza
 * @since 20.08.2025
 */

@Entity
@Table(name = "search_appearance")
@Getter
@Setter
@ToString
public class SearchAppearance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @Column(name = "searcher_id", insertable = false, updatable = false)
    private Long searcherId;

    @ManyToOne
    @JoinColumn(name = "searcher_id", nullable = false)
    private User searcher;

    @Column(name = "searched_id", insertable = false, updatable = false)
    private Long searchedId;

    @ManyToOne
    @JoinColumn(name = "searched_id", nullable = false)
    private User searched;

    @Column(name = "searched_at")
    private LocalDateTime searchedAt;
}
