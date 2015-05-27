package lib.lennken.utils;

import java.util.regex.Pattern;

/**
 * Created by caprinet on 11/13/14.
 */
public class Validations {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean validMail(String email) {
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }
}
