package com.pulimed.renderer.session;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RenderSessionService {
    private final Map<UUID, RenderSession> sessions = new ConcurrentHashMap<>();

    public UUID create(String backend) {
        UUID id = UUID.randomUUID();
        sessions.put(id, new RenderSession(backend));
        return id;
    }

    public RenderSession require(UUID id) {
        RenderSession session = sessions.get(id);
        if (session == null) {
            throw new RenderSessionNotFoundException(id);
        }
        return session;
    }

    public void delete(UUID id) {
        RenderSession session = sessions.remove(id);
        if (session != null) {
            session.close();
        }
    }

    @PreDestroy
    public void closeAll() {
        sessions.values().forEach(RenderSession::close);
        sessions.clear();
    }
}
