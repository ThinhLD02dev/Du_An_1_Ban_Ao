/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Utility class for common validation operations
 */
public class ValidationUtils {
    
    /**
     * Safely convert Object to Integer
     */
    public static int toInteger(Object obj, int defaultValue) {
        if (obj == null) return defaultValue;
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Safely convert Object to BigDecimal
     */
    public static BigDecimal toBigDecimal(Object obj, BigDecimal defaultValue) {
        if (obj == null) return defaultValue;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Number) {
            return new BigDecimal(((Number) obj).doubleValue());
        }
        try {
            return new BigDecimal(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Validate email format using regex
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validate Vietnamese phone number
     * Accepts: 0xxxxxxxxx, +84xxxxxxxxx, 84xxxxxxxxx
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        String phoneRegex = "^(0|\\+84|84)\\d{9}$";
        return phone.matches(phoneRegex);
    }

    /**
     * Validate currency input
     */
    public static boolean isValidCurrency(String value) {
        if (value == null || value.isEmpty()) return false;
        String currencyRegex = "^\\d+(\\.\\d{1,2})?$";
        return value.matches(currencyRegex);
    }

    /**
     * Remove all formatting from currency string
     */
    public static String stripCurrencyFormat(String value) {
        if (value == null) return "";
        return value.trim().replace(",", "").replace(".", "");
    }

    /**
     * Format number as currency (VND)
     */
    public static String formatCurrency(BigDecimal value) {
        if (value == null) return "0.00";
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        return df.format(value);
    }

    /**
     * Format number as currency (VND) without decimals
     */
    public static String formatCurrencyWhole(BigDecimal value) {
        if (value == null) return "0";
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0");
        return df.format(value);
    }

    /**
     * Safely get value from map with default
     */
    public static <T> T getOrDefault(Map<String, Object> map, String key, T defaultValue) {
        try {
            Object value = map.getOrDefault(key, defaultValue);
            return (T) value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Check if string is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Normalize string (trim + compress spaces)
     */
    public static String normalize(String str) {
        if (isEmpty(str)) return "";
        return str.trim().replaceAll("\\s+", " ");
    }
}
