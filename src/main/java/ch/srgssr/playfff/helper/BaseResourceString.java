package ch.srgssr.playfff.helper;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public class BaseResourceString {
    public static String getString(ApplicationContext applicationContext, String name) {
        try {
            Resource resource = applicationContext.getResource("classpath:" + name);
            if (resource == null) {
                throw new RuntimeException("No resource: " + name);
            }
            String s = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return s;
        } catch (IOException e) {
            throw new RuntimeException("IO Exception for " + name, e);
        }
    }
}
