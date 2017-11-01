package wiresegal.classy.hats.common.misc

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.EnumStringSerializable
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory
import com.teamwizardry.librarianlib.features.saving.Module
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.*
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import wiresegal.classy.hats.common.hat.ItemHat
import wiresegal.classy.hats.common.misc.BlockHatStand.StandMaterial.*

/**
 * @author WireSegal
 * Created at 9:54 PM on 9/4/17.
 */
object BlockHatStand : BlockModContainer("hat_stand", Material.ROCK, *StandMaterial.values().map { "hat_stand_${it.getName()}" }.toTypedArray()) {

    init {
        setHardness(2.0F)
        setResistance(5.0F)
    }

    override fun getBurnTime(stack: ItemStack): Int {
        val variant = StandMaterial.values()[stack.itemDamage % StandMaterial.values().size]
        return if (variant == OAK ||
                variant == SPRUCE ||
                variant == BIRCH ||
                variant == JUNGLE ||
                variant == ACACIA ||
                variant == DARK_OAK)
            300
        else 0
    }

    enum class StandMaterial : EnumStringSerializable {
        OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, STONE, QUARTZ, OBSIDIAN
    }

    private var madeProperty = false
    lateinit var PROPERTY: PropertyEnum<StandMaterial>
        private set

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        val te = worldIn.getTileEntity(pos) as TileHatStand
        val xS = placer.posX - (te.pos.x + 0.5)
        val zS = placer.posZ - (te.pos.z + 0.5)
        val angle = MathHelper.atan2(zS, xS) * 180 / Math.PI + 90
        te.angle = Math.round(angle / 22.5) * 22.5f
    }

    override fun isSideSolid(baseState: IBlockState?, world: IBlockAccess?, pos: BlockPos?, side: EnumFacing?): Boolean {
        return side == EnumFacing.UP
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos) as TileHatStand

        if (playerIn.isSneaking) {
            if (!worldIn.isRemote) {
                val xS = playerIn.posX - (te.pos.x + 0.5)
                val zS = playerIn.posZ - (te.pos.z + 0.5)
                val angle = MathHelper.atan2(zS, xS) * 180 / Math.PI + 90
                te.angle = Math.round(angle / 22.5) * 22.5f
                te.markDirty()
            }
            return true
        }

        val stack = playerIn.getHeldItem(hand)

        var ret = false
        val invStack = te.inv.handler.getStackInSlot(0)

        if (stack.item == ItemHat) {
            val xS = playerIn.posX - (te.pos.x + 0.5)
            val zS = playerIn.posZ - (te.pos.z + 0.5)
            val angle = MathHelper.atan2(zS, xS) * 180 / Math.PI + 90
            te.angle = Math.round(angle / 22.5) * 22.5f

            te.inv.handler.setStackInSlot(0, stack.copy().apply { count = 1 })
            playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, ItemStack.EMPTY)

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

    override fun getBlockFaceShape(world: IBlockAccess, state: IBlockState, pos: BlockPos, facing: EnumFacing?): BlockFaceShape {
        return if (facing == EnumFacing.UP) BlockFaceShape.SOLID else if (facing == EnumFacing.DOWN) BlockFaceShape.CENTER_BIG else BlockFaceShape.UNDEFINED
    }

    override fun isTopSolid(state: IBlockState): Boolean {
        return true
    }

    fun getCollisionBoxes(): List<AxisAlignedBB> {
        val list = mutableListOf<AxisAlignedBB>()

        fun addBox(x0: Int, y0: Int, z0: Int, x1: Int, y1: Int, z1: Int) {
            val baseX = x0 / 16.0
            val baseY = y0 / 16.0
            val baseZ = z0 / 16.0
            val targetX = x1 / 16.0
            val targetY = y1 / 16.0
            val targetZ = z1 / 16.0
            list.add(AxisAlignedBB(baseX, baseY, baseZ, targetX, targetY, targetZ))
        }

        addBox(0, 12, 0, 16, 16, 16)
        addBox(4, 10, 4, 12, 12, 12)
        addBox(5,  4, 5, 11, 10, 11)
        addBox(3,  0, 3, 13,  4, 13)
        return list
    }

    override fun collisionRayTrace(blockState: IBlockState, worldIn: World, pos: BlockPos, start: Vec3d, end: Vec3d)
            = getCollisionBoxes()
            .map { rayTrace(pos, start, end, it) }
            .firstOrNull { it != null }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, actual: Boolean) {
        for (box in getCollisionBoxes())
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box)
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

    override fun hasComparatorInputOverride(state: IBlockState?) = true

    override fun createBlockState(): BlockStateContainer {
        if (!madeProperty) {
            PROPERTY = PropertyEnum.create("material", StandMaterial::class.java)
            madeProperty = true
        }
        return BlockStateContainer(this, PROPERTY)
    }

    override fun damageDropped(state: IBlockState) = getMetaFromState(state)

    override fun getStateFromMeta(meta: Int): IBlockState
            = defaultState.withProperty(PROPERTY, StandMaterial.values()[meta % StandMaterial.values().size])

    override fun getMetaFromState(state: IBlockState) = state.getValue(PROPERTY).ordinal

    override fun getMaterial(state: IBlockState): Material
            = when (state.getValue(PROPERTY)) {
                OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK -> Material.WOOD
                else -> Material.ROCK
            }

    override fun getBlockHardness(state: IBlockState, worldIn: World, pos: BlockPos)
            = when (worldIn.getBlockState(pos).getValue(PROPERTY)) {
                OBSIDIAN -> 22.5F
                STONE -> 1.5F
                QUARTZ -> 0.8F
                else -> super.getBlockHardness(state, worldIn, pos)
            }

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion)
            = when (world.getBlockState(pos).getValue(PROPERTY)) {
                OBSIDIAN -> 2000F * 3 / 5
                STONE -> 10F * 3 / 5
                QUARTZ -> 4F * 3 / 5
                else -> super.getExplosionResistance(world, pos, exploder, explosion)
            }

    override fun getHarvestLevel(state: IBlockState): Int {
        if (state.getValue(PROPERTY) == OBSIDIAN) return 3
        return super.getHarvestLevel(state)
    }

    override fun isFullCube(state: IBlockState) = false
    override fun isOpaqueCube(blockState: IBlockState) = false

    override fun getSoundType(state: IBlockState, world: World, pos: BlockPos, entity: Entity?): SoundType
            = when (state.getValue(PROPERTY)) {
                OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK -> SoundType.WOOD
                else -> SoundType.STONE
            }

    override fun createTileEntity(world: World, state: IBlockState) = TileHatStand()

    @TileRegister
    class TileHatStand : TileMod() {
        @Module
        val inv = ModuleInventory(object : ItemStackHandler() {
            override fun getStackLimit(slot: Int, stack: ItemStack) = if (stack.item == ItemHat) 1 else 0

            override fun onContentsChanged(slot: Int) = markDirty()
        })

        override fun getRenderBoundingBox(): AxisAlignedBB = AxisAlignedBB(pos, pos.add(1, 2, 1))

        @Save
        var angle = 0f
    }
}
