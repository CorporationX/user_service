package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.randomAvatar.AvatarService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.user.UserFilterValidation;
import school.faang.user_service.validator.user.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserFilterValidation userFilterValidation;
    private final UserMapper userMapper;
    private final AvatarService avatarService;
    private final CountryRepository countryRepository;
    private final GoalService goalService;

    private final UserValidator userValidator;

    @Transactional
    public UserDto createUser(UserDto userDto, MultipartFile avatar) {
        Country country = countryRepository.findById(userDto.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Country with %s id doesn't exist", userDto.getCountryId())));

        User userToSave = userMapper.toEntity(userDto);
        userToSave.setCountry(country);

        userRepository.save(userToSave);

        if (avatar == null) {
            avatarService.setAvatar(userDto, null);
        } else {
            avatarService.setAvatar(userDto, avatarService.convertMultipartFileToBytes(avatar));
        }

        userToSave.setUserProfilePic(userDto.getUserProfilePic());
        User savedUser = userRepository.save(userToSave);

        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        if (userFilterValidation.isNullable(userFilterDto)) {
            throw new DataValidationException("UserFilter is nullable");
        }

        List<User> premiumUsers = userRepository.findPremiumUsers().toList();

        if (!userFilterValidation.isAnyFilterApplicable(userFilters, userFilterDto)) {
            return userMapper.toDtoList(premiumUsers);
        }

        return filterUsers(userFilterDto, premiumUsers);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getRegularUsers(UserFilterDto userFilterDto) {
        if (userFilterValidation.isNullable(userFilterDto)) {
            throw new DataValidationException("UserFilter is nullable");
        }

        List<User> regularUsers = userRepository.findRegularUsers().toList();

        if (!userFilterValidation.isAnyFilterApplicable(userFilters, userFilterDto)) {
            return userMapper.toDtoList(regularUsers);
        }

        return filterUsers(userFilterDto, regularUsers);
    }

    private List<UserDto> filterUsers(UserFilterDto userFilterDto, List<User> users) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(userFilterDto))
                .reduce(users.stream(),
                        (stream, filter) -> filter.apply(stream, userFilterDto),
                        Stream::concat)
                .map(userMapper::toDto)
                .toList();

    }

    @Transactional
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public void existUserById(long id) {
        if (!userRepository.existsById(id)) {
            log.error("User with id :{} doesn't exist!", id);
            throw new EntityNotFoundException("User with id :" + id + " doesn't exist!");
        }
    }

    @Transactional(readOnly = true)
    public UserDto getById(Long userId) {
        User foundedUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with %s id doesn't exist", userId)));

        return userMapper.toDto(foundedUser);
    }

    @Transactional
    public byte[] getAvatar(Long userId) {
        UserDto userById = getById(userId);
        String avatarId = userById.getUserProfilePic().getSmallFileId();

        return avatarService.get(avatarId);
    }

    @Transactional
    public void deactivateUser(long userId) {

        User user = userRepository.findById(userId).orElseThrow();
        user.setActive(false);
        userRepository.save(user);
        goalService.removeUserGoals(userId);

    }
}