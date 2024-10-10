package org.example.coffee.config;

import io.jsonwebtoken.SignatureAlgorithm;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;

public class VNPAYConfig {
    private static final String securityKey = "afnkndskfknadsnfksfnisafsafjefiwe";
    public static String hasAllFields(Map<String, String> fields) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();
            stringBuilder.append(field).append("=").append(value).append("&");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return BCrypt.hashpw(stringBuilder.toString(), securityKey);
    }
}
