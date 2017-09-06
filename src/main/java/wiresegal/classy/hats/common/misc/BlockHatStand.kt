package wiresegal.classy.hats.common.misc

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.EnumStringSerializable
import com.teamwizardry.librarianlib.features.base.block.TileMod
import com.teamwizardry.librarianlib.features.base.block.module.ModuleInventory
import com.teamwizardry.librarianlib.features.saving.Module
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.Explosion
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import wiresegal.classy.hats.common.hat.ItemHat
import wiresegal.classy.hats.common.misc.BlockHatStand.StandMaterial.*

/**
 * @author WireSegal
 * Created at 9:54 PM on 9/4/17.
 */
object BlockHatStand : BlockModContainer("hat_stand", Material.WOOD, *StandMaterial.values().map { "hat_stand_${it.getName()}" }.toTypedArray()) {

    init {
        setHardness(2.0F)
        setResistance(5.0F)
    }

    enum class StandMaterial : EnumStringSerializable {
        OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, STONE, QUARTZ, OBSIDIAN
    }

    var PROPERTY: PropertyEnum<StandMaterial>? = null
        private set

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        val facing = placer.horizontalFacing
        val angle = facing.horizontalAngle
        val te = worldIn.getTileEntity(pos) as TileHatStand
        te.angle = angle
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos) as TileHatStand

        if (playerIn.isSneaking) {
            val xS = playerIn.posX - (te.pos.x + 0.5)
            val zS = playerIn.posZ - (te.pos.z + 0.5)
            te.angle = (MathHelper.atan2(zS, xS) * 180 / Math.PI + 90).toFloat()
            te.markDirty()
            return true
        }

        val stack = playerIn.getHeldItem(hand)

        var ret = false
        val invStack = te.inv.handler.getStackInSlot(0)

        if (stack.item == ItemHat) {
            te.inv.handler.setStackInSlot(0, stack.copy().apply { count = 1 })
            stack.count--

            ret = true
        }

        if (!invStack.isEmpty) {
            val copy = invStack.copy()
            if (playerIn.heldItemMainhand.isEmpty)
                playerIn.setHeldItem(EnumHand.MAIN_HAND, copy)
            else if (!playerIn.inventory.addItemStackToInventory(copy))
                InventoryHelper.spawnItemStack(worldIn, pos.x.toDouble(), pos.y + 0.75, pos.z.toDouble(), copy)

            if (!ret)
                te.inv.handler.setStackInSlot(0, ItemStack.EMPTY)
            ret = true
        }

        return ret
    }

    override fun getComparatorInputOverride(blockState: IBlockState, worldIn: World, pos: BlockPos): Int {
        val capability = (worldIn.getTileEntity(pos) as TileHatStand).inv.handler
        var percent = 0f
        for (i in 0 until capability.slots) {
            val inSlot = capability.getStackInSlot(i)
            percent += inSlot.count.toFloat() / getMaxStackSize(i, capability, inSlot)
        }
        percent /= capability.slots
        return (percent * 15).toInt() + if (percent > 0.0) 1 else 0
    }

    private fun getMaxStackSize(slot: Int, handler: IItemHandler, inSlot: ItemStack?): Int {
        if (inSlot == null || inSlot.isEmpty) return 64
        val stack = inSlot.copy()
        stack.count = inSlot.maxStackSize - inSlot.count
        val result = handler.insertItem(slot, stack, true)
        return inSlot.maxStackSize - result.count
    }

    override fun hasComparatorInputOverride(state: IBlockState) = true

    override fun createBlockState(): BlockStateContainer {
        if (PROPERTY == null)
            PROPERTY = PropertyEnum.create("material", StandMaterial::class.java)
        return BlockStateContainer(this, PROPERTY)
    }

    override fun damageDropped(state: IBlockState) = getMetaFromState(state)

    override fun getStateFromMeta(meta: Int): IBlockState
            = defaultState.withProperty(PROPERTY, StandMaterial.values()[meta % StandMaterial.values().size])

    override fun getMetaFromState(state: IBlockState) = state.getValue(PROPERTY).ordinal

    override fun getMaterial(state: IBlockState): Material {
        return when (state.getValue(PROPERTY)) {
            OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK -> Material.WOOD
            else -> Material.ROCK
        }
    }

    override fun getBlockHardness(state: IBlockState, worldIn: World, pos: BlockPos): Float {
        if (state.getValue(PROPERTY) == OBSIDIAN) return 50F
        return super.getBlockHardness(state, worldIn, pos)
    }

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion): Float {
        if (world.getBlockState(pos).getValue(PROPERTY) == OBSIDIAN) return 2000F
        return super.getExplosionResistance(world, pos, exploder, explosion)
    }

    override fun getHarvestLevel(state: IBlockState): Int {
        if (state.getValue(PROPERTY) == OBSIDIAN) return 3
        return super.getHarvestLevel(state)
    }

    override fun isFullCube(state: IBlockState) = false
    override fun isOpaqueCube(blockState: IBlockState) = false

    override fun getSoundType(state: IBlockState, world: World, pos: BlockPos, entity: Entity?): SoundType {
        return when (state.getValue(PROPERTY)) {
            OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK -> SoundType.WOOD
            else -> SoundType.STONE
        }
    }

    override fun createTileEntity(world: World, state: IBlockState) = TileHatStand()

    @TileRegister
    class TileHatStand : TileMod() {
        @Module
        val inv = ModuleInventory(object : ItemStackHandler() {
            override fun getStackLimit(slot: Int, stack: ItemStack): Int {
                return if (stack.item == ItemHat) 1 else 0
            }

            override fun onContentsChanged(slot: Int) {
                markDirty()
            }
        })

        @Save
        var angle = 0f
    }
}
