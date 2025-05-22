package school.faang.user_service.service.event;

import com.querydsl.core.BooleanBuilder;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.event.QEvent;
import school.faang.user_service.entity.promotion.event.QEventPromotion;

@Component
public class EventBoolBuilderConstructor {
    public BooleanBuilder construct(EventFilterDto filter) {
        QEvent qEvent = QEvent.event;
        QEventPromotion qEventPromotion = QEventPromotion.eventPromotion;
        BooleanBuilder builder = new BooleanBuilder();

        if (filter.getStartDate() != null) {
            builder = builder.and(qEvent.startDate.goe(filter.getStartDate()));
        }
        if (filter.getEndDate() != null) {
            builder = builder.and(qEvent.endDate.loe(filter.getEndDate()));
        }
        if (filter.getLocation() != null) {
            builder = builder.and(qEvent.location.eq(filter.getLocation()));
        }
        if (filter.getMaxAttendees() != null) {
            builder = builder.and(qEvent.maxAttendees.eq(filter.getMaxAttendees()));
        }
        if (filter.getEventType() != null) {
            builder = builder.and(qEvent.type.eq(EventType.valueOf(filter.getEventType())));
        }
        return null;

/*        if (filter.getAverageRate() != null) {
            builder = builder
        }*/

    }

}
