package dev.huskuraft.effortless.compat.sable;

import java.util.List;
import dev.huskuraft.universal.api.core.BlockPosition;
import dev.huskuraft.effortless.renderer.outliner.BlockClusterOutline;

public final class PreviewCluster extends BlockClusterOutline implements Cloneable {
    private final List<BlockPosition> positions;

    public PreviewCluster(List<BlockPosition> snapshot) {
        super(snapshot);
        positions = snapshot;
    }

    public boolean matches(List<?> next) {
        return positions.equals(next);
    }

    public PreviewCluster withFreshStyle() {
        // The upstream cluster is immutable after construction. Keep the per-entry
        // parameters independent, including styles held by previous callers.
        try {
            var copy = (PreviewCluster) super.clone();
            copy.params = new OutlineParams();
            return copy;
        } catch (CloneNotSupportedException impossible) {
            throw new AssertionError(impossible);
        }
    }
}
