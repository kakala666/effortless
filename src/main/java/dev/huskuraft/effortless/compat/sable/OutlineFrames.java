package dev.huskuraft.effortless.compat.sable;

import java.util.Map;
import java.util.WeakHashMap;
import dev.huskuraft.universal.api.core.BlockPosition;
import dev.huskuraft.universal.api.renderer.Renderer;
import dev.huskuraft.universal.api.math.*;
import dev.huskuraft.effortless.renderer.outliner.Outline;
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import net.minecraft.client.Minecraft;

public final class OutlineFrames {
    private record Anchor(Frame frame, BlockPosition pos) {}
    private static final Map<Outline, Anchor> anchors = new WeakHashMap<>();
    private static final ThreadLocal<Vector3d> camera = new ThreadLocal<>();

    public static void bind(Outline outline, BlockPosition pos) {
        var level = Minecraft.getInstance().level;
        var frame = level == null ? null : Frame.at(level, Frame.pos(pos));
        if (frame == null || frame.owner() == null) anchors.remove(outline);
        else anchors.put(outline, new Anchor(frame, pos));
    }

    public static Vector3d localCamera() { return camera.get(); }

    public static void render(Outline outline, Renderer renderer, float tick) {
        var anchor = anchors.get(outline);
        if (anchor == null) { outline.render(renderer, tick); return; }
        var level = Minecraft.getInstance().level;
        if (level == null || !anchor.frame.contains(level, anchor.pos)) return;
        if (!(anchor.frame.ship(level, anchor.pos) instanceof ClientSubLevelAccess ship)) return;
        var pose = ship.renderPose();
        var localEye = pose.transformPositionInverse(TraceScope.vec(renderer.getCamera().position()));
        var q = pose.orientation();
        renderer.pushPose();
        camera.set(TraceScope.vector(localEye));
        try {
            renderer.rotate(new Quaternionf((float)q.x(), (float)q.y(), (float)q.z(), (float)q.w()));
            renderer.scale(pose.scale().x(), pose.scale().y(), pose.scale().z());
            outline.render(renderer, tick);
        } finally {
            camera.remove();
            renderer.popPose();
        }
    }
}
