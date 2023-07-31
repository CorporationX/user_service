package school.faang.user_service.util;

public class ProfilePicGenerator {

    public static String generateProfilePic(String nameFirstLetter, String surnameFirstLetter){
        String seed = nameFirstLetter + surnameFirstLetter;
        String avatarUrl = "https://api.dicebear.com/6.x/initials/svg?seed=" + seed
                + "&radius=20&backgroundType=gradientLinear";
        System.out.println(avatarUrl);

        return avatarUrl;
    }
}
