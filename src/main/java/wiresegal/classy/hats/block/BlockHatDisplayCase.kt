package wiresegal.classy.hats.block

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

class BlockHatDisplayCase : BlockHatContainer("hat_display_case") {
    override fun getPickBlock(side: EnumFacing?, hat: ItemStack, container: ItemStack) = container

    override fun getCollisionBoxes(state: IBlockState) = listOf(
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
            AxisAlignedBB(0.0, 1.0, 0.0, 1.0, 1.875, 1.0)
    )

    override fun getBlockLayer() = BlockRenderLayer.CUTOUT
}
