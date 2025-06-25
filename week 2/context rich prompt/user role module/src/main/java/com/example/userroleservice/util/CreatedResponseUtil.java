package com.example.userroleservice.util;

import jakarta.ws.rs.core.Response;
import java.net.URI;

public class CreatedResponseUtil {
    public static String getCreatedId(Response response) {
        URI location = response.getLocation();
        if (location == null) {
            return null;
        }
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
} 