package school.faang.user_service.entity.promotion;

import jakarta.persistence.*;
import lombok.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

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

    @Column(name = "impressions")
    private int impressions;

    @Enumerated(EnumType.STRING)
    @Column(name = "audience_reach")
    private AudienceReach audienceReach;
}
