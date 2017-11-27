package wiresegal.classy.hats.block

import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockHatStand : BlockHatContainer("hat_stand") {
    override fun getPickBlock(side: EnumFacing?, hat: ItemStack, container: ItemStack)
            = if (side == EnumFacing.UP && hat.isNotEmpty) hat else container

    override fun getCollisionBoxes(state: IBlockState) = listOf(
            AxisAlignedBB(0.0, 0.75, 0.0, 1.0, 1.0, 1.0),
            AxisAlignedBB(0.25, 0.625, 0.25, 0.75, 0.75, 0.75),
            AxisAlignedBB(0.3125, 0.25, 0.3125, 0.6875, 0.625, 0.6875),
            AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.25, 0.8125)
    )

    override fun isFullCube(state: IBlockState) = false

    override fun isOpaqueCube(blockState: IBlockState) = false

    override fun getBlockFaceShape(world: IBlockAccess, state: IBlockState, pos: BlockPos, facing: EnumFacing?): BlockFaceShape {
        return when (facing) {
            EnumFacing.UP -> BlockFaceShape.SOLID
            EnumFacing.DOWN -> BlockFaceShape.CENTER_BIG
            else -> BlockFaceShape.UNDEFINED
        }
    }
}
