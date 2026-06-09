package com.pulimed.renderer.nativebridge;

import java.nio.file.Files;
import java.nio.file.Path;

public final class NativeRenderer {
    static {
        String explicitPath = System.getProperty("renderer.native.library");
        Path projectNativeLibrary = Path.of("native", "renderer_jni.dll").toAbsolutePath().normalize();

        if (explicitPath != null && !explicitPath.isBlank()) {
            System.load(Path.of(explicitPath).toAbsolutePath().normalize().toString());
        } else if (Files.isRegularFile(projectNativeLibrary)) {
            System.load(projectNativeLibrary.toString());
        } else {
            System.loadLibrary("renderer_jni");
        }
    }

    private NativeRenderer() {
    }

    public static native long create(String backend);
    public static native void destroy(long handle);
    public static native void initialize(long handle);
    public static native void setVolume(long handle, byte[] data, int width, int height, int depth,
                                        int windowWidth, int windowCenter, double spacing, double thickness);
    public static native void resizeViewport(long handle, int index, int width, int height);
    public static native void setSlice(long handle, int index,
                                       float ox, float oy, float oz,
                                       float ux, float uy, float uz,
                                       float vx, float vy, float vz);
    public static native void rotate(long handle, float dx, float dy);
    public static native void render(long handle, int mask);
    public static native byte[] getImage(long handle, int index);
}
