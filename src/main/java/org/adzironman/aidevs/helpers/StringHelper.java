package org.adzironman.aidevs.helpers;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringHelper {
    public String extractUrl(String text) {
        String urlPattern = "http[s]?://\\S+";
        Pattern pattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        if (urlMatcher.find()) {
            return text.substring(urlMatcher.start(0), urlMatcher.end(0));
        } else {
            throw new NullPointerException("Can not get URL from null object");
        }

    }
}
