package dk.nykredit.api.capabilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SanitizerTest {

    @Test
    public void testOkInput() {
        assertEquals("This is ok input", Sanitizer.sanitize("This is ok input", true, true));
        assertEquals("This is ok input 2", Sanitizer.sanitize("This is ok input 2", true, true));
    }

    @Test
    public void testNullInput() {
        assertEquals("", Sanitizer.sanitize(null, true, true));
        assertEquals("", Sanitizer.sanitize(null, false, true));
        assertEquals("", Sanitizer.sanitize(null, true, false));
        assertEquals("", Sanitizer.sanitize(null, false, false));
    }

    @Test
    public void testInputWithoutNumbers() {
        assertEquals("This is not ok input", Sanitizer.sanitize("This is not ok input", true, false));
        assertEquals("", Sanitizer.sanitize("This is not ok input with a number 2", true, false));
    }

    @Test
    public void testInputWithoutSpace() {
        assertEquals("ThisIsOKInput", Sanitizer.sanitize("ThisIsOKInput", false, true));
        assertEquals("ThisIsOKInput2", Sanitizer.sanitize("ThisIsOKInput2", false, true));
        assertEquals("Thisisokinput", Sanitizer.sanitize("This is ok input", false, true));
    }

    @Test
    public void testInputWithoutSpaceAndNumbers() {
        assertEquals("ThisIsOKInput", Sanitizer.sanitize("ThisIsOKInput", false, false));
        assertEquals("", Sanitizer.sanitize("ThisIsOKInput2", false, false));
        assertEquals("Thisisokinput", Sanitizer.sanitize("This is ok input", false, false));
    }

    @Test
    public void testSuspiciousInput() {
        assertEquals("This is ok input", Sanitizer.sanitize("This is ok input", true, true));
        assertEquals("This is ok input 2", Sanitizer.sanitize("This is ok input 2", true, true));
        assertEquals("", Sanitizer.sanitize("This is not ok input 2 '", true, true));
        assertEquals("", Sanitizer.sanitize("This is not ok input 2 'OR 1", true, true));
    }
}
