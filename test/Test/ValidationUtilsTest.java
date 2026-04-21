package Test;

import org.junit.Test;
import static org.junit.Assert.*;
import util.ValidationUtils;

public class ValidationUtilsTest {
    
    @Test
    public void testValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("user@example.com"));
        assertTrue(ValidationUtils.isValidEmail("test.user@domain.co.uk"));
        assertFalse(ValidationUtils.isValidEmail("invalid@"));
        assertFalse(ValidationUtils.isValidEmail("@example.com"));
        assertFalse(ValidationUtils.isValidEmail("noemail"));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail(null));
    }
    
    @Test
    public void testValidPhoneNumber() {
        assertTrue(ValidationUtils.isValidPhoneNumber("0912345678"));
        assertTrue(ValidationUtils.isValidPhoneNumber("+84912345678"));
        assertTrue(ValidationUtils.isValidPhoneNumber("84912345678"));
        assertFalse(ValidationUtils.isValidPhoneNumber("091234567"));    // 9 digits
        assertFalse(ValidationUtils.isValidPhoneNumber("09123456789"));  // 11 digits
        assertFalse(ValidationUtils.isValidPhoneNumber("+9123456789"));  // wrong prefix
        assertFalse(ValidationUtils.isValidPhoneNumber(""));
        assertFalse(ValidationUtils.isValidPhoneNumber(null));
    }
    
    @Test
    public void testIsValidCurrency() {
        assertTrue(ValidationUtils.isValidCurrency("100"));
        assertTrue(ValidationUtils.isValidCurrency("100.50"));
        assertTrue(ValidationUtils.isValidCurrency("1000.99"));
        assertFalse(ValidationUtils.isValidCurrency("100.500"));  // 3 decimals
        assertFalse(ValidationUtils.isValidCurrency("abc"));
        assertFalse(ValidationUtils.isValidCurrency(""));
    }
    
    @Test
    public void testToInteger() {
        assertEquals(100, ValidationUtils.toInteger(100, 0));
        assertEquals(100, ValidationUtils.toInteger(100L, 0));
        assertEquals(100, ValidationUtils.toInteger("100", 0));
        assertEquals(-1, ValidationUtils.toInteger(null, -1));
        assertEquals(-1, ValidationUtils.toInteger("abc", -1));
    }
    
    @Test
    public void testNormalize() {
        assertEquals("Hello World", ValidationUtils.normalize("  Hello    World  "));
        assertEquals("", ValidationUtils.normalize(null));
        assertEquals("", ValidationUtils.normalize("   "));
    }
}