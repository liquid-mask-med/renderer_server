package com.pulimed.renderer.api;

import com.pulimed.renderer.session.RenderSession;
import com.pulimed.renderer.session.RenderSessionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/render/sessions")
public class RenderSessionController {
    private final RenderSessionService sessions;

    public RenderSessionController(RenderSessionService sessions) {
        this.sessions = sessions;
    }

    @PostMapping
    public Map<String, UUID> create(@RequestParam(defaultValue = "Vulkan") String backend) {
        return Map.of("sessionId", sessions.create(backend));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        sessions.delete(id);
    }

    @PutMapping(path = "/{id}/volume", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void setVolume(@PathVariable UUID id,
                          @RequestParam int width,
                          @RequestParam int height,
                          @RequestParam int depth,
                          @RequestParam int windowWidth,
                          @RequestParam int windowCenter,
                          @RequestParam double spacing,
                          @RequestParam double thickness,
                          @RequestBody byte[] volume) {
        sessions.require(id).setVolume(volume, width, height, depth, windowWidth, windowCenter, spacing, thickness);
    }

    @PutMapping("/{id}/viewports/{index}")
    public void resizeViewport(@PathVariable UUID id, @PathVariable int index,
                               @RequestParam int width, @RequestParam int height) {
        sessions.require(id).resizeViewport(index, width, height);
    }

    @PutMapping("/{id}/slices/{index}")
    public void setSlice(@PathVariable UUID id, @PathVariable int index,
                         @RequestBody RenderSession.SliceState slice) {
        sessions.require(id).setSlice(index, slice);
    }

    @PostMapping("/{id}/rotate")
    public void rotate(@PathVariable UUID id, @RequestParam float dx, @RequestParam float dy) {
        sessions.require(id).rotate(dx, dy);
    }

    @PostMapping(path = "/{id}/views/{index}/render", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> render(@PathVariable UUID id, @PathVariable int index) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(sessions.require(id).render(index));
    }
}
