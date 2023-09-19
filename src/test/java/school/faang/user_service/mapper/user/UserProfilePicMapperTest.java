package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.entity.UserProfilePic;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserProfilePicMapperTest {
    @Spy
    private UserProfilePicMapperImpl userProfilePicMapper;
    private UserProfilePic userProfilePic;
    private UserProfilePicDto userProfilePicDto;

    @BeforeEach
    void setUp() {
        userProfilePicDto = UserProfilePicDto.builder()
                .fileId("fileId")
                .smallFileId("smallFileId")
                .build();

        userProfilePic = UserProfilePic.builder()
                .fileId("fileId")
                .smallFileId("smallFileId")
                .build();
    }

    @Test
    void testToDto() {
        assertEquals(userProfilePicDto, userProfilePicMapper.toDto(userProfilePic));
    }

    @Test
    void testToEntity() {
        assertEquals(userProfilePic, userProfilePicMapper.toEntity(userProfilePicDto));
    }
}