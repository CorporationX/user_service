package school.faang.user_service.mapper.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class MenteeMapperTest {

    private MenteeMapper menteeMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        menteeMapper = new MenteeMapperImpl();
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("one");
        testUser.setEmail("one@email");
        testUser.setPhone("123456789");
        testUser.setAboutMe("about one");
        testUser.setCity("Moscow");
        testUser.setExperience(10);
    }

    @Test
    void userToMenteeDto_ShouldProperlyMapUserToMenteeDto() {
        MenteeDto menteeDto = menteeMapper.userToMenteeDto(testUser);

        assertAll(() -> {
            assertNotNull(menteeDto);
            assertEquals(menteeDto.getUsername(), testUser.getUsername());
            assertEquals(menteeDto.getEmail(), testUser.getEmail());
            assertEquals(menteeDto.getPhone(), testUser.getPhone());
            assertEquals(menteeDto.getAboutMe(), testUser.getAboutMe());
            assertEquals(menteeDto.getCity(), testUser.getCity());
            assertEquals(menteeDto.getExperience(), testUser.getExperience());
        });
    }
}