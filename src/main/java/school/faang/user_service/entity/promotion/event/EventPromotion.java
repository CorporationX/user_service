package school.faang.user_service.entity.promotion.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.PromotionBase;

@Entity
@Table(name = "event_promotion")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@DiscriminatorValue("EVENT_PROMOTION")
@PrimaryKeyJoinColumn(name = "id")
public class EventPromotion extends PromotionBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
}
