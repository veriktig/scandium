package org.junit.tests.validation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

@SuppressWarnings("deprecation")
public class BadlyFormedClassesTest {
    public static class FaultyConstructor {
        public FaultyConstructor() throws Exception {
            throw new Exception("Thrown during construction");
        }

        @Test
        public void someTest() {
            /*
                * Empty test just to fool JUnit and IDEs into running this class as
                * a JUnit test
                */
        }
    }

    ;

    @RunWith(JUnit4ClassRunner.class)
    public static class BadBeforeMethodWithLegacyRunner {
        @Before
        void before() {

        }

        @Test
        public void someTest() {
        }
    }

    ;

    public static class NoTests {
        // class without tests
    }

    @Test
    public void constructorException() {
        String message = exceptionMessageFrom(FaultyConstructor.class);
        assertEquals("Thrown during construction", message);
    }

    @Test
    public void noRunnableMethods() {
        assertThat(exceptionMessageFrom(NoTests.class), containsString("No runnable methods"));
    }

    @Test
    public void badBeforeMethodWithLegacyRunner() {
        assertEquals("Method before should be public",
                exceptionMessageFrom(BadBeforeMethodWithLegacyRunner.class));
    }

    private String exceptionMessageFrom(Class<?> testClass) {
        JUnitCore core = new JUnitCore();
        Result result = core.run(testClass);
        Failure failure = result.getFailures().get(0);
        String message = failure.getException().getMessage();
        return message;
    }
}
