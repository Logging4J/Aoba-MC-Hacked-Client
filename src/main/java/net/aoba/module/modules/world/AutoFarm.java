/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * AutoFarm Module
 */
package net.aoba.module.modules.world;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.ModuleUtils;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoFarm extends Module implements TickListener
{

    private FloatSetting radius = FloatSetting.builder().id("autofarm_radius").displayName("Radius")
            .description("Radius").defaultValue(5f).minValue(0f).maxValue(15f).step(1f).build();

    public AutoFarm()
    {
        super("AutoFarm");
        this.setCategory(Category.of("World"));
        this.setDescription("Automatically plants, fertilizes, and harvests crops.");
        this.addSetting(radius);
    }

    public void setRadius(int radius)
    {
        this.radius.setValue((float) radius);
    }

    @Override
    public void onDisable()
    {
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
    }

    @Override
    public void onEnable()
    {
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onToggle()
    {
    }

    @Override
    public void onTick(Pre event)
    {
        int rad = radius.getValue().intValue();
        BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();

        for (int x = -rad; x < rad; x++)
        {
            for (int y = -1; y <= 1; y++)
            {
                for (int z = -rad; z < rad; z++)
                {
                    mutableBlockPos.set(MC.player.getBlockPos().getX() + x,
                            MC.player.getBlockPos().getY() + y,
                            MC.player.getBlockPos().getZ() + z);
                    Block block = MC.world.getBlockState(mutableBlockPos).getBlock();
                    BlockState blockState = MC.world.getBlockState(mutableBlockPos);

                    if (block instanceof CropBlock)
                    {
                        CropBlock crop = (CropBlock) block;
                        if (!crop.canGrow(MC.world, null, mutableBlockPos, blockState))
                        {
                            MC.player.networkHandler.sendPacket(
                                    new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, mutableBlockPos, Direction.NORTH));
                            MC.player.networkHandler.sendPacket(
                                    new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, mutableBlockPos, Direction.NORTH));
                        }
                        else
                        {
                            fertilizeCrops(mutableBlockPos);
                        }
                    }
                    else if (block instanceof FarmlandBlock)
                    {
                        handleFarmland(mutableBlockPos, x, y, z);
                    }
                }
            }
        }
    }

    private void fertilizeCrops(BlockPos.Mutable mutableBlockPos)
    {
        boolean hasBoneMeal = false;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = MC.player.getInventory().getStack(i);
            if (stack.getItem() == Items.BONE_MEAL)
            {
                MC.player.getInventory().selectedSlot = i;
                hasBoneMeal = true;
                break;
            }
        }
        if (hasBoneMeal)
        {
            BlockHitResult rayTrace = new BlockHitResult(Vec3d.ZERO, Direction.UP, mutableBlockPos, false);
            MC.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, rayTrace, 0));
        }
    }

    private void handleFarmland(BlockPos.Mutable mutableBlockPos, int x, int y, int z)
    {
        BlockPos.Mutable blockAbovePos = mutableBlockPos.move(Direction.UP);
        Block blockAbove = MC.world.getBlockState(blockAbovePos).getBlock();
        if (blockAbove == Blocks.AIR)
        {
            boolean hasSeed = false;
            for (int i = 0; i < 9; i++)
            {
                ItemStack stack = MC.player.getInventory().getStack(i);
                if (ModuleUtils.isPlantable(stack))
                {
                    MC.player.getInventory().selectedSlot = i;
                    hasSeed = true;
                    break;
                }
            }
            if (hasSeed)
            {
                BlockHitResult rayTrace = new BlockHitResult(Vec3d.ZERO, Direction.UP, mutableBlockPos, false);
                MC.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, rayTrace, 0));
            }
        }
    }


    @Override
    public void onTick(Post event)
    {

    }
}