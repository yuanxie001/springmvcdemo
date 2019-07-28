package other;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PetternTest {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

    public static void main(String[] args) {
        String email = "abc@zhumu.me";
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        boolean b = matcher.find();
        System.out.println(b);
    }

}
