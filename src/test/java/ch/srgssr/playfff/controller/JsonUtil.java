package ch.srgssr.playfff.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtil {
    // Lay Initialization
    private static volatile ObjectMapper mapper = null;

    // Prevent multiple instances
    private JsonUtil() {}

    /**
     * Double-checked locking
     * @return
     */
    public static synchronized ObjectMapper getMapper() {
        if (mapper == null) {
            synchronized (JsonUtil.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
                }
            }
        }
        return mapper;
    }
}
