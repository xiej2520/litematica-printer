package me.aleksilassila.litematica.printer.guide.guides;

import me.aleksilassila.litematica.printer.enums.BlockMatchResult;
import me.aleksilassila.litematica.printer.guide.Guide;
import me.aleksilassila.litematica.printer.guide.Result;
import me.aleksilassila.litematica.printer.printer.SchematicBlockContext;
import me.aleksilassila.litematica.printer.printer.action.Action;
//#if MC >= 11600
import net.minecraft.world.level.portal.PortalShape;
//#endif
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.NetherPortalBlock;

/**
 * 下界传送门
 */
public class NetherPortalGuide extends Guide {

    public NetherPortalGuide(SchematicBlockContext context) {
        super(context);
    }

    @Override
    protected Result onBuildActionMissingBlock(BlockMatchResult state) {
        Direction.Axis requiredAxis = getProperty(requiredState, NetherPortalBlock.AXIS)
                .orElse(Direction.Axis.X);
        //#if MC >= 11600
        boolean canCreatePortal = PortalShape.findEmptyPortalShape(level, blockPos, requiredAxis).isPresent();
        //#else
        //$$ boolean canCreatePortal = new NetherPortalBlock.PortalShape(level, blockPos, requiredAxis).isValid();
        //#endif
        if (canCreatePortal) {
            return Result.success(new Action()
                    .setItems(Items.FLINT_AND_STEEL, Items.FIRE_CHARGE)
                    .setRequiresSupport());
        }
        return Result.SKIP;
    }

    @Override
    protected Result onBuildActionWrongState(BlockMatchResult state) {
        return Result.SKIP;
    }
}
