package school.faang.user_service.mapper.mentorship;

import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MentorMapperTest {
    private MentorMapper mentorMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        mentorMapper = new MentorMapperImpl(new MenteeMapperImpl());
        testUser = getTestUser();
    }

    @Test
    void userToMentorDto_shouldProperlyMapMentorFields() {
        MentorDto mentorDto = mentorMapper.userToMentorDto(testUser);

        assertAll(() -> {
            assertNotNull(mentorDto);
            assertEquals(mentorDto.getUsername(), testUser.getUsername());
            assertEquals(mentorDto.getEmail(), testUser.getEmail());
            assertEquals(mentorDto.getPhone(), testUser.getPhone());
            assertEquals(mentorDto.getAboutMe(), testUser.getAboutMe());
            assertEquals(mentorDto.getCity(), testUser.getCity());
            assertEquals(mentorDto.getExperience(), testUser.getExperience());
        });
    }

    @Test
    void userToMentorDto_shouldProperlyMapMenteeList() {
        MentorDto mentorDto = mentorMapper.userToMentorDto(testUser);

        assertAll(() -> {
            assertEquals(mentorDto.getMentees().size(), 3);
            assertEquals(mentorDto.getMentees().get(0).getUsername(), "mentee#1");
            assertEquals(mentorDto.getMentees().get(1).getUsername(), "mentee#2");
            assertEquals(mentorDto.getMentees().get(2).getUsername(), "mentee#3");
        });
    }

    private User getTestUser() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("one");
        testUser.setEmail("one@email");
        testUser.setPhone("123456789");
        testUser.setAboutMe("about one");
        testUser.setCity("Moscow");
        testUser.setExperience(10);
        testUser.setMentees(
                IntStream.rangeClosed(1, 3)
                        .mapToObj(i -> {
                            User user = new User();
                            user.setUsername("mentee#" + i);
                            return user;
                        })
                        .toList()
        );
        return testUser;
    }
}