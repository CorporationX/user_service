package school.faang.user_service.exception;

public class AvatarFetchException extends RuntimeException {
    private static final String AVATAR_FETCH_EXCEPTION_MSG = "Failed to fetch avatar from Dicebear API at URL ";

    public AvatarFetchException(String url) {
        super(AVATAR_FETCH_EXCEPTION_MSG + url);
    }
}
