package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;

public class FilterTest {

    @Test
    public void checkIsApplicableInEventDescriptionFilter() {
        EventDescriptionFilter descriptionFilter = new EventDescriptionFilter();
        EventFilterDto filterDto = EventFilterDto.builder().descriptionPattern("test1").build();

        Assertions.assertTrue(descriptionFilter.isApplicable(filterDto));
    }
}
