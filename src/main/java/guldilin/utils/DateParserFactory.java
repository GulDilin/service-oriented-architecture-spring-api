package guldilin.utils;

import guldilin.errors.ErrorCode;
import guldilin.errors.ErrorMessage;
import guldilin.errors.ValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DateParserFactory {
    public static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static Date parseDate(String s, String fieldName) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        if (s.length() > dateFormat.length()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put(fieldName, ErrorCode.WRONG_DATE_FORMAT.name() + " (" + dateFormat + "): " + ErrorMessage.WRONG_DATE_FORMAT);
            throw new IllegalArgumentException(new ValidationException(errors));
        }
        try {
            return formatter.parse(s);
        } catch (ParseException e) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put(fieldName, ErrorCode.WRONG_DATE_FORMAT.name() + " (" + dateFormat + "): " + e.getMessage());
            throw new IllegalArgumentException(new ValidationException(errors));
        }
    }
}
