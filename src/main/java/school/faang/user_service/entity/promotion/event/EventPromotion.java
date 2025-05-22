package school.faang.user_service.entity.promotion.event;

import jakarta.persistence.*;
import lombok.*;
import school.faang.user_service.entity.promotion.PromotionBase;
import school.faang.user_service.entity.event.Event;

@Entity
@Table(name = "event_promotion")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@NamedEntityGraph(
        name = "EventPromotion.withUserAndEvent",
        attributeNodes = {
                @NamedAttributeNode("client"),
                @NamedAttributeNode("event")
        }
)
@DiscriminatorValue("EVENT_PROMOTION")
@PrimaryKeyJoinColumn(name = "id")
public class EventPromotion extends PromotionBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
}
