package za.ac.cput.util;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class Helper {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static void validateNotNull(Object obj, String errorMessage) {
        if (isNull(obj)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void validateNotNullOrEmpty(String str, String errorMessage) {
        if (isNullOrEmpty(str)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void validateEmail(String email, String errorMessage) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static boolean isValidID(int patientID) {
        return patientID > 0;
    }

    public static boolean isValidList(List<Integer> appointmentsIDs) {
        return appointmentsIDs != null && !appointmentsIDs.isEmpty();
    }
}
