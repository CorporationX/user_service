package school.faang.user_service.entity.promotion;

import jakarta.persistence.*;
import lombok.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "promotional_plan", nullable = false)
    private PromotionalPlan promotionalPlan;

    @Column(name = "impressions", nullable = false)
    private int impressions;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endDate;
}
