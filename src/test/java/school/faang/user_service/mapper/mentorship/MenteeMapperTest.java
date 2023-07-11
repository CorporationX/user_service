package school.faang.user_service.mapper.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class MenteeMapperTest {

    private MenteeMapper menteeMapper;

    @BeforeEach
    void setUp() {
    menteeMapper = new MenteeMapperImpl();
    }

    @Test
    void userToMenteeDto_ShouldProperlyMapUserToMenteeDto() {
        User testUserOne = new User();
        testUserOne.setId(1L);
        testUserOne.setUsername("one");
        testUserOne.setEmail("one@email");
        testUserOne.setPhone("123456789");
        testUserOne.setAboutMe("about one");

        MenteeDto menteeDto = menteeMapper.UserToMenteeDto(testUserOne);

        assertAll(() -> {
            assertNotNull(menteeDto);
            assertEquals(menteeDto.getUsername(), testUserOne.getUsername());
            assertEquals(menteeDto.getEmail(), testUserOne.getEmail());
            assertEquals(menteeDto.getPhone(), testUserOne.getPhone());
            assertEquals(menteeDto.getAboutMe(), testUserOne.getAboutMe());
        });
    }
}