package dev.huskuraft.effortless.test;

import dev.huskuraft.effortless.Effortless;
import dev.huskuraft.effortless.building.*;
import dev.huskuraft.effortless.building.session.BatchBuildSession;
import dev.huskuraft.effortless.api.core.*;
import dev.huskuraft.effortless.building.structure.builder.standard.Single;
import dev.huskuraft.effortless.neoforge.core.MinecraftPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.*;

@Mod("effortless_sable_test")
@GameTestHolder("effortless_sable_test")
public class PlainTest {
    @PrefixGameTestTemplate(false)
    @GameTest(template="empty")
    public static void placeAndUndoWithoutSable(GameTestHelper test) {
        test.assertTrue(Effortless.PROTOCOL_VERSION == 13, "Changed vanilla protocol");
        var level = test.getLevel();
        var target = test.absolutePos(new BlockPos(2, 2, 2));
        var fake = FakePlayerFactory.getMinecraft(level);
        fake.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
        fake.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new net.minecraft.world.item.ItemStack(Blocks.STONE_BRICKS));
        var player = new MinecraftPlayer(fake);
        var pos = new BlockPosition(target.getX(), target.getY(), target.getZ());
        var hit = new BlockInteraction(pos.toVector3d(), Direction.UP, pos, false);
        var context = Context.defaultSet().withBuildState(BuildState.PLACE_BLOCK).withPlayerExtras(player)
                .withStructure(new Single()).withNextInteraction(hit);
        var placed = new BatchBuildSession(Effortless.getInstance(), player, context).commit();
        test.assertTrue(level.getBlockState(target).is(Blocks.STONE_BRICKS), "Failed to place");
        placed.getReverseOperation().commit();
        test.assertTrue(level.getBlockState(target).isAir(), "Failed to undo");
        test.succeed();
    }
}
