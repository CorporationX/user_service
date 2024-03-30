package school.faang.user_service.testFail;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TestPipline {
    @Test
    public void testFail(){
        int result = 3+2;
        Assert.assertEquals(5, result);
    }
}
