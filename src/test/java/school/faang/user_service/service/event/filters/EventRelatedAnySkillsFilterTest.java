package school.faang.user_service.service.event.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.test_data.event.TestDataEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRelatedAnySkillsFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventRelatedAnySkillsFilter eventRelatedAnySkillsFilter;

    private List<Skill> skillList;

    @BeforeEach
    void setUp() {
        eventRelatedAnySkillsFilter = new EventRelatedAnySkillsFilter();
        TestDataEvent testDataEvent = new TestDataEvent();
        Skill skill1 = testDataEvent.getSkill1();
        Skill skill2 = testDataEvent.getSkill2();
        skillList = List.of(skill1, skill2);
    }

    @Nested
    class PositiveTests {
        @Test
        void testIsApplicable_Success() {
            when(filter.getRelatedAnySkillsPattern()).thenReturn("test");

            boolean result = eventRelatedAnySkillsFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter, atLeastOnce()).getRelatedAnySkillsPattern();
        }

        @Test
        public void testApplyFilter_Success() {
            when(event.getRelatedSkills()).thenReturn(skillList);
            when(filter.getRelatedAnySkillsPattern()).thenReturn("skill");

            boolean result = eventRelatedAnySkillsFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getRelatedSkills();
            verify(filter, atLeastOnce()).getRelatedAnySkillsPattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setRelatedAnySkillsPattern(null);

            assertFalse(eventRelatedAnySkillsFilter.isApplicable(filter));
        }

        @Test
        public void testIsApplicable_BlankPattern_AssertFalse() {
            filter.setRelatedAnySkillsPattern(" ");

            assertFalse(eventRelatedAnySkillsFilter.isApplicable(filter));
        }

        @Test
        public void testApplyFilter_NoMatch_AssertFalse() {
            when(event.getRelatedSkills()).thenReturn(skillList);
            when(filter.getRelatedAnySkillsPattern()).thenReturn("non-skill");

            boolean result = eventRelatedAnySkillsFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event, atLeastOnce()).getRelatedSkills();
            verify(filter, atLeastOnce()).getRelatedAnySkillsPattern();
        }
    }
}
