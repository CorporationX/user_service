package school.faang.user_service.aspect.util;

import org.aspectj.lang.ProceedingJoinPoint;

public class AspectUtils {

    public static <T> T extractArgument(ProceedingJoinPoint joinPoint, Class<T> clazz) {
        T value = findArgumentOfType(joinPoint, clazz);
        if (value == null) {
            throw new IllegalArgumentException(String.format("Argument of type %s not founded in joinPoint: %s",
                    clazz.getSimpleName(), joinPoint.getSignature()));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static <T> T findArgumentOfType(ProceedingJoinPoint joinPoint, Class<T> clazz) {
        for (Object arg : joinPoint.getArgs()) {
            if (clazz.isInstance(arg)) {
                return (T) arg;
            }
        }
        return null;
    }
}
