package ch.srgssr.playfff.helper;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by seb on 10/08/16.
 */
public class BaseResourceString {
    public static String getString(ApplicationContext applicationContext, String name, Map<String, String> variables) {
        try {
            Resource resource = applicationContext.getResource("classpath:" + name);
            if (resource == null) {
                throw new RuntimeException("No resource: " + name);
            }
            String s = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            for (String k : variables.keySet()) {
                String value = variables.get(k);
                if (value == null) {
                    throw new IllegalArgumentException(k + " has null value (" + name + ")");
                }
                s = s.replaceAll(k, value);
            }
            return s;
        } catch (IOException e) {
            throw new RuntimeException("IO Exception for " + name, e);
        }
    }
}
