package dev.huskuraft.effortless.compat.sable;

import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

/** Read-only view used exclusively during tessellation of an Effortless ship preview. */
public final class PreviewLightView implements BlockAndTintGetter {
    private final ClientLevel level;
    private final ClientSubLevel ship;
    private final Pose3dc pose;
    private int chunkX = Integer.MIN_VALUE, chunkZ;
    private boolean local;

    PreviewLightView(ClientLevel level, ClientSubLevel ship, Pose3dc pose) {
        this.level = level;
        this.ship = ship;
        this.pose = pose;
    }

    boolean matches(ClientLevel level, ClientSubLevel ship) { return this.level == level && this.ship == ship; }

    private BlockPos lightPosition(BlockPos pos) {
        int x = pos.getX() >> 4, z = pos.getZ() >> 4;
        if (chunkX != x || chunkZ != z) {
            var plot = ship.getPlot();
            local = plot.getChunkHolder(plot.toLocal(new ChunkPos(x,z))) != null;
            chunkX = x;
            chunkZ = z;
        }
        return local ? pos : BlockPos.containing(pose.transformPosition(Vec3.atCenterOf(pos)));
    }

    @Override public int getBrightness(LightLayer layer, BlockPos pos) { return level.getBrightness(layer, lightPosition(pos)); }
    @Override public int getRawBrightness(BlockPos pos, int darken) { return level.getRawBrightness(lightPosition(pos), darken); }
    @Override public LevelLightEngine getLightEngine() { return level.getLightEngine(); }
    @Override public int getHeight() { return level.getHeight(); }
    @Override public int getMinBuildHeight() { return level.getMinBuildHeight(); }
    @Override public float getShade(Direction direction, boolean shade) { return level.getShade(direction, shade); }
    @Override public float getShade(float x,float y,float z,boolean shade) { return level.getShade(x,y,z,shade); }
    @Override public int getBlockTint(BlockPos pos, ColorResolver resolver) { return level.getBlockTint(pos,resolver); }
    @Override public BlockEntity getBlockEntity(BlockPos pos) { return level.getBlockEntity(pos); }
    @Override public BlockState getBlockState(BlockPos pos) { return level.getBlockState(pos); }
    @Override public FluidState getFluidState(BlockPos pos) { return level.getFluidState(pos); }
    @Override public ModelData getModelData(BlockPos pos) { return level.getModelData(pos); }
    @Override public AuxiliaryLightManager getAuxLightManager(ChunkPos pos) { return level.getAuxLightManager(pos); }
}
