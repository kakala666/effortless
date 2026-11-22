package dev.huskuraft.effortless.compat.sable;

import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.huskuraft.effortless.building.Context;

public final class PreviewPoses {
    private static final ThreadLocal<PreviewPoses> ACTIVE = new ThreadLocal<>();
    private SubLevelAccess lastShip;
    private Pose3dc lastPose;
    private PreviewLightView lighting;

    public static PreviewLightView lighting(net.minecraft.client.multiplayer.ClientLevel level,
                                             dev.ryanhcode.sable.sublevel.ClientSubLevel ship) {
        var scope = ACTIVE.get();
        if (scope == null) return new PreviewLightView(level,ship,ship.renderPose());
        if (scope.lighting == null || !scope.lighting.matches(level,ship))
            scope.lighting = new PreviewLightView(level,ship,get(ship));
        return scope.lighting;
    }
    private Context lastContext;
    private int lastVolume;

    public static int volume(Context context) {
        var batch = ACTIVE.get();
        if (batch == null) return context.getVolume();
        if (batch.lastContext != context) {
            batch.lastVolume = context.getVolume();
            batch.lastContext = context;
        }
        return batch.lastVolume;
    }

    public static void duringRender(Runnable render) {
        var previous = ACTIVE.get();
        ACTIVE.set(new PreviewPoses());
        try {
            render.run();
        } finally {
            if (previous == null) ACTIVE.remove(); else ACTIVE.set(previous);
        }
    }

    public static Pose3dc get(SubLevelAccess ship) {
        var scope = ACTIVE.get();
        if (scope != null && scope.lastShip == ship) return scope.lastPose;
        var pose = ship instanceof ClientSubLevelAccess c ? c.renderPose() : ship.logicalPose();
        if (scope != null) {
            scope.lastShip = ship;
            scope.lastPose = pose;
        }
        return pose;
    }
}
