package school.faang.user_service.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.aspect.util.AspectUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AspectUtilsTest {

    @Mock
    ProceedingJoinPoint joinPoint;

    static class Dummy {}

    @Test
    void extractArgument_shouldReturnExpectedArgument() {
        Dummy expected = new Dummy();
        Object[] args = { "string", expected, 123 };

        Mockito.when(joinPoint.getArgs()).thenReturn(args);

        Dummy result = AspectUtils.extractArgument(joinPoint, Dummy.class);

        assertThat(result).isSameAs(expected);
    }

    @Test
    void extractArgument_shouldThrowIfNotFound() {
        Object[] args = { "string", 123 };

        Signature signature = Mockito.mock(Signature.class);
        Mockito.when(signature.toString()).thenReturn("mockMethod()");
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(joinPoint.getArgs()).thenReturn(args);

        assertThatThrownBy(() -> AspectUtils.extractArgument(joinPoint, Dummy.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Argument of type Dummy not founded")
                .hasMessageContaining("mockMethod()");
    }
}
