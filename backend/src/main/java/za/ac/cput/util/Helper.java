/* Helper.java
   Utilities for validations and parsing (JPA + UUID friendly)
   Author: team
*/
package za.ac.cput.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public final class Helper {
    private Helper() {}

    // ------- Patterns -------
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // ------- Basic checks -------
    public static boolean isNull(Object o) { return o == null; }
    public static boolean isNullOrEmpty(String s) { return s == null || s.trim().isEmpty(); }
    public static boolean isNullOrEmpty(Collection<?> c) { return c == null || c.isEmpty(); }

    public static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
    public static void requireNotNull(Object o, String message) {
        if (o == null) throw new IllegalArgumentException(message);
    }
    public static void requireNotBlank(String s, String message) {
        if (isNullOrEmpty(s)) throw new IllegalArgumentException(message);
    }

    // ------- Email -------
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    public static void requireEmail(String email, String message) {
        if (!isValidEmail(email)) throw new IllegalArgumentException(message);
    }

    // ------- UUID helpers -------
    public static UUID parseUuid(String value, String fieldName) {
        requireNotBlank(value, fieldName + " must not be blank");
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid UUID");
        }
    }

    // If you have Optional<Entity> and want a uniform exception:
    public static <T> T requirePresent(Optional<T> optional, String what) {
        return optional.orElseThrow(() -> new IllegalArgumentException(what + " not found"));
    }

    // ------- Date/Time parsing (ISO-8601) -------
    public static LocalDate parseDate(String value, String fieldName) {
        requireNotBlank(value, fieldName + " must not be blank");
        try { return LocalDate.parse(value.trim()); }
        catch (DateTimeParseException ex) { throw new IllegalArgumentException(fieldName + " must be ISO date (YYYY-MM-DD)"); }
    }

    public static LocalTime parseTime(String value, String fieldName) {
        requireNotBlank(value, fieldName + " must not be blank");
        try { return LocalTime.parse(value.trim()); }
        catch (DateTimeParseException ex) { throw new IllegalArgumentException(fieldName + " must be ISO time (HH:mm or HH:mm:ss)"); }
    }

    public static LocalDateTime parseDateTime(String value, String fieldName) {
        requireNotBlank(value, fieldName + " must not be blank");
        try { return LocalDateTime.parse(value.trim()); }
        catch (DateTimeParseException ex) { throw new IllegalArgumentException(fieldName + " must be ISO datetime (YYYY-MM-DDTHH:mm[:ss])"); }
    }

    public static void requireFuture(LocalDateTime dt, String fieldName) {
        requireNotNull(dt, fieldName + " must not be null");
        if (!dt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(fieldName + " must be in the future");
        }
    }
    public static void requireFuture(LocalDate date, String fieldName) {
        requireNotNull(date, fieldName + " must not be null");
        if (!date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(fieldName + " must be a future date");
        }
    }

    // ------- Collections -------
    public static boolean isNotEmpty(Collection<?> c) { return c != null && !c.isEmpty(); }
    public static <T> void requireNotEmpty(Collection<T> c, String message) {
        if (c == null || c.isEmpty()) throw new IllegalArgumentException(message);
    }

    // ------- Legacy int helpers (kept for old tests; prefer UUID) -------
    @Deprecated
    public static boolean isPositiveNumber(int n) { return n > 0; }
    @Deprecated
    public static void requirePositiveNumber(int n, String message) {
        if (n <= 0) throw new IllegalArgumentException(message);
    }
    @Deprecated
    public static int generateAppointmentID() {
        // Legacy only: entities now use @GeneratedValue(UUID)
        return Math.abs(UUID.randomUUID().hashCode());
    }

    // ------- Misc -------
    public static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    public static String lower(String s) { return s == null ? null : s.toLowerCase(); }
    public static <T> T requireNonNull(T obj, String message) { return Objects.requireNonNull(obj, message); }
}
