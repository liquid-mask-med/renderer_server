package com.pulimed.renderer.session;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RenderSessionNotFoundException extends RuntimeException {
    public RenderSessionNotFoundException(UUID id) {
        super("Render session not found: " + id);
    }
}
