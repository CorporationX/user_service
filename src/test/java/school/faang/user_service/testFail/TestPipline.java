package school.faang.user_service.testFail;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TestPipline {
    @Test
    public void testFail(){
        int result = 2+3;
        Assert.assertEquals(5, result);
    }
}
