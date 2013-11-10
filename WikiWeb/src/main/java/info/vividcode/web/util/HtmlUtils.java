package info.vividcode.web.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {

    public static final Pattern HTML_SPECIAL_CHAR_PATTERN = Pattern.compile("[&<>\"']");
    public static final Map<Character,String> CHAR_ESCAPED_MAP = new HashMap<>(7);
    static {
        CHAR_ESCAPED_MAP.put('&', "&amp;");
        CHAR_ESCAPED_MAP.put('<', "&lt;");
        CHAR_ESCAPED_MAP.put('>', "&gt;");
        CHAR_ESCAPED_MAP.put('"', "&quot;");
        CHAR_ESCAPED_MAP.put('\'', "&#39;");
    }

    public static String h(String str) {
        Matcher m = HTML_SPECIAL_CHAR_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            int idx = m.toMatchResult().start();
            char lastMatchedChar = str.charAt(idx);
            m.appendReplacement(sb, CHAR_ESCAPED_MAP.get(lastMatchedChar));
        }
        m.appendTail(sb);
        return sb.toString();
    }

}
