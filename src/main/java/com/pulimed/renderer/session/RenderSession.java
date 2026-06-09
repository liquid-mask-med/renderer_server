package com.pulimed.renderer.session;

import com.pulimed.renderer.nativebridge.NativeRenderer;

public final class RenderSession implements AutoCloseable {
    private final java.util.concurrent.ExecutorService executor;
    private final long handle;

    public RenderSession(String backend) {
        executor = java.util.concurrent.Executors.newSingleThreadExecutor(
                Thread.ofPlatform().name("render-session-", 0).factory());
        handle = call(() -> {
            long nativeHandle = NativeRenderer.create(backend);
            NativeRenderer.initialize(nativeHandle);
            return nativeHandle;
        });
    }

    public void setVolume(byte[] data, int width, int height, int depth,
                          int windowWidth, int windowCenter, double spacing, double thickness) {
        run(() -> NativeRenderer.setVolume(handle, data, width, height, depth, windowWidth, windowCenter, spacing, thickness));
    }

    public void resizeViewport(int index, int width, int height) {
        run(() -> NativeRenderer.resizeViewport(handle, index, width, height));
    }

    public void setSlice(int index, SliceState slice) {
        run(() -> NativeRenderer.setSlice(handle, index,
                slice.origin().x(), slice.origin().y(), slice.origin().z(),
                slice.axisU().x(), slice.axisU().y(), slice.axisU().z(),
                slice.axisV().x(), slice.axisV().y(), slice.axisV().z()));
    }

    public void rotate(float dx, float dy) {
        run(() -> NativeRenderer.rotate(handle, dx, dy));
    }

    public byte[] render(int viewIndex) {
        return call(() -> {
            NativeRenderer.render(handle, 1 << viewIndex);
            return NativeRenderer.getImage(handle, viewIndex);
        });
    }

    @Override
    public void close() {
        try {
            run(() -> NativeRenderer.destroy(handle));
        } finally {
            executor.shutdown();
        }
    }

    private void run(Runnable action) {
        call(() -> {
            action.run();
            return null;
        });
    }

    private <T> T call(java.util.concurrent.Callable<T> action) {
        try {
            return executor.submit(action).get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Render session operation interrupted", ex);
        } catch (java.util.concurrent.ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Render session operation failed", cause);
        }
    }

    public record Vec3(float x, float y, float z) {
    }

    public record SliceState(Vec3 origin, Vec3 axisU, Vec3 axisV) {
    }
}
