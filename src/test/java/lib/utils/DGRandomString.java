package lib.utils;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class DGRandomString {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final SecureRandom random = new SecureRandom();

    public String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

    public Map<String, String> generateAuthData() {
        Map<String, String> authMap = new HashMap<>();

        authMap.put("email", generateRandomString(10) + "@test.com");
        authMap.put("password", generateRandomString(5));
        authMap.put("username", generateRandomString(5));
        authMap.put("firstName", generateRandomString(5));
        authMap.put("lastName", generateRandomString(5));

        return authMap;
    }

    public Map<String, String> generateAuthData(Map<String, String> givenValues) {
        Map<String, String> defaultValues = generateAuthData();

        for (Map.Entry<String, String> pair : givenValues.entrySet()) {
            if (nonNull(defaultValues.get(pair.getKey()))) {
                defaultValues.replace(pair.getKey(), pair.getValue());
            } else {
                defaultValues.put(pair.getKey(), pair.getValue());
            }
        }

        return defaultValues;
    }
}
