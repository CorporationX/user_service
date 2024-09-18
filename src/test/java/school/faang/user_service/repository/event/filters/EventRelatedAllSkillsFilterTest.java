package school.faang.user_service.repository.event.filters;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRelatedAllSkillsFilterTest {
    @Mock
    private Event event;
    @Mock
    private EventFilterDto filter;
    @InjectMocks
    private EventRelatedAllSkillsFilter eventRelatedAllSkillsFilter;

    private List<Skill> skillList;

    @BeforeEach
    void setUp() {
        eventRelatedAllSkillsFilter = new EventRelatedAllSkillsFilter();
        TestDataEvent testDataEvent = new TestDataEvent();
        Skill skill1 = testDataEvent.getSkill1();
        Skill skill2 = testDataEvent.getSkill2();
        skillList = List.of(skill1, skill2);
    }

    @Nested
    class PositiveTests {
        @Test
        void testIsApplicable_Success() {
            when(filter.getRelatedAllSkillsPattern()).thenReturn("test");

            boolean result = eventRelatedAllSkillsFilter.isApplicable(filter);
            assertTrue(result);

            verify(filter, atLeastOnce()).getRelatedAllSkillsPattern();
        }

        @Test
        public void testApplyFilter_Success() {
            when(event.getRelatedSkills()).thenReturn(skillList);
            when(filter.getRelatedAllSkillsPattern()).thenReturn("skill");

            boolean result = eventRelatedAllSkillsFilter.applyFilter(event, filter);
            assertTrue(result);

            verify(event, atLeastOnce()).getRelatedSkills();
            verify(filter, atLeastOnce()).getRelatedAllSkillsPattern();
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testIsApplicable_NullPattern_AssertFalse() {
            filter.setRelatedAllSkillsPattern(null);

            assertFalse(eventRelatedAllSkillsFilter.isApplicable(filter));
        }

        @Test
        public void testIsApplicable_BlankPattern_AssertFalse() {
            filter.setRelatedAllSkillsPattern(" ");

            assertFalse(eventRelatedAllSkillsFilter.isApplicable(filter));
        }

        @Test
        public void testApplyFilter_NoMatch_AssertFalse() {
            when(event.getRelatedSkills()).thenReturn(skillList);
            when(filter.getRelatedAllSkillsPattern()).thenReturn("non-skill");

            boolean result = eventRelatedAllSkillsFilter.applyFilter(event, filter);
            assertFalse(result);

            verify(event, atLeastOnce()).getRelatedSkills();
            verify(filter, atLeastOnce()).getRelatedAllSkillsPattern();
        }
    }
}