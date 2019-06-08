package wiresegal.classyhats.block

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.EnumStringSerializable
import com.teamwizardry.librarianlib.features.base.block.ItemModBlock
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory
import com.teamwizardry.librarianlib.features.saving.Module
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.*
import net.minecraft.world.Explosion
import net.minecraft.world.World
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler
import wiresegal.classyhats.ClassyHats
import wiresegal.classyhats.item.ItemHat

abstract class BlockHatContainer(name: String) : BlockModContainer(name, Material.AIR, *ContainerMaterial.getVariantStrings(name)) {
    abstract fun getPickBlock(side: EnumFacing?, hat: ItemStack, container: ItemStack): ItemStack

    abstract fun getCollisionBoxes(state: IBlockState): List<AxisAlignedBB>

    override fun createTileEntity(world: World, state: IBlockState) = getTileEntity(state)

    open fun getTileEntity(state: IBlockState) : TileHatContainer = TileHatContainer()

    override fun createItemForm(): ItemBlock? = object : ItemModBlock(this) {
        override fun getTranslationKey(stack: ItemStack): String {
            val name = when {
                stack.itemDamage >= variants.size -> bareName
                else -> variants[stack.itemDamage]
            }
            return "tile.${ClassyHats.ID}.$name"
        }
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult?, world: World, pos: BlockPos, player: EntityPlayer): ItemStack {
        val hatStack = (world.getTileEntity(pos) as TileHatContainer).inventory.handler.getStackInSlot(0).copy()
        return getPickBlock(target?.sideHit, hatStack, ItemStack(this, 1, state.getValue(MATERIAL).ordinal))
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile = worldIn.getTileEntity(pos) as TileHatContainer
        val handler = tile.inventory.handler
        val heldStack = playerIn.getHeldItem(hand)
        val invStack = handler.getStackInSlot(0)

        if (playerIn.isSneaking) {
            if (!worldIn.isRemote) {
                val xS = playerIn.posX - (tile.pos.x + 0.5)
                val zS = playerIn.posZ - (tile.pos.z + 0.5)
                val angle = MathHelper.atan2(zS, xS) * 180 / Math.PI + 90
                tile.angle = Math.round(angle / 22.5) * 22.5f
                tile.markDirty()
            }
        } else {
            if (!invStack.isEmpty) {
                val copy = invStack.copy()
                if (playerIn.heldItemMainhand.isEmpty)
                    playerIn.setHeldItem(EnumHand.MAIN_HAND, copy)
                else if (!playerIn.addItemStackToInventory(copy))
                    Block.spawnAsEntity(worldIn, pos, copy)
                handler.setStackInSlot(0, ItemStack.EMPTY)
            }
            if (heldStack.item is ItemHat && invStack.isEmpty) {
                val xS = playerIn.posX - (tile.pos.x + 0.5)
                val zS = playerIn.posZ - (tile.pos.z + 0.5)
                val angle = MathHelper.atan2(zS, xS) * 180 / Math.PI + 90
                tile.angle = Math.round(angle / 22.5) * 22.5f
                handler.setStackInSlot(0, heldStack.copy().apply { count = 1 })
                playerIn.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1.0F, 1.0F)
                playerIn.setHeldItem(hand, heldStack.copy().apply { shrink(1) })
            }
        }
        return (!heldStack.isEmpty && heldStack.item is ItemHat) || !invStack.isEmpty
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        val tile = worldIn.getTileEntity(pos) as TileHatContainer
        val xS = placer.posX - (tile.pos.x + 0.5)
        val zS = placer.posZ - (tile.pos.z + 0.5)
        val angle = MathHelper.atan2(zS, xS) * 180 / Math.PI + 90
        tile.angle = Math.round(angle / 22.5) * 22.5f
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, MATERIAL)
    }

    override fun damageDropped(state: IBlockState) = getMetaFromState(state)

    override fun getStateFromMeta(meta: Int): IBlockState =
            defaultState.withProperty(MATERIAL, ContainerMaterial.getSafely(meta))

    override fun getMetaFromState(state: IBlockState) = state.getValue(MATERIAL).ordinal

    override fun getMaterial(state: IBlockState): Material = state.getValue(MATERIAL).material

    override fun getSoundType(state: IBlockState, world: World, pos: BlockPos, entity: Entity?)
            = state.getValue(MATERIAL).soundType

    override fun getBlockHardness(state: IBlockState, world: World, pos: BlockPos)
            = world.getBlockState(pos).getValue(MATERIAL).hardness

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion)
            = world.getBlockState(pos).getValue(MATERIAL).resistance

    override fun getHarvestLevel(state: IBlockState): Int = state.getValue(MATERIAL).harvestLevel

    override fun getBurnTime(stack: ItemStack): Int {
        return if (ContainerMaterial.getSafely(stack.itemDamage).material.canBurn) 300 else 0
    }

    override fun getComparatorInputOverride(blockState: IBlockState, worldIn: World, pos: BlockPos): Int {
        val tile = (worldIn.getTileEntity(pos) as TileHatContainer)
        val power = ItemHandlerHelper.calcRedstoneFromInventory(tile.inventory.handler) / 15f
        return tile.getPowerLevel(power)
    }

    override fun hasComparatorInputOverride(state: IBlockState) = true

    override fun collisionRayTrace(blockState: IBlockState, world: World, pos: BlockPos, start: Vec3d, end: Vec3d): RayTraceResult? {
        return getCollisionBoxes(blockState).map { rayTrace(pos, start, end, it) }.firstOrNull { it != null }
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, actual: Boolean) {
        getCollisionBoxes(state).forEach { addCollisionBoxToList(pos, entityBox, collidingBoxes, it) }
    }

    @TileRegister
    open class TileHatContainer : TileMod() {
        @Save
        var angle = 0.0F

        @Module
        val inventory = ModuleInventory(object : ItemStackHandler() {
            override fun getStackLimit(slot: Int, stack: ItemStack) = if (stack.item is ItemHat) 1 else 0

            override fun onContentsChanged(slot: Int) = markDirty()
        })

        override fun getRenderBoundingBox() = AxisAlignedBB(pos, pos.add(1, 2, 1))
    }

    companion object {
        val MATERIAL: PropertyEnum<ContainerMaterial> = PropertyEnum.create("material", ContainerMaterial::class.java)

        enum class ContainerMaterial(
                val material: Material = Material.WOOD,
                val soundType: SoundType = SoundType.WOOD,
                val hardness: Float = 2.0F,
                val resistance: Float = 5.0F,
                val harvestLevel: Int = 0
        ) : EnumStringSerializable {
            OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK,
            STONE(Material.ROCK, SoundType.STONE, 1.5F, 30.0F),
            QUARTZ(Material.ROCK, SoundType.STONE, 0.8F, 4.0F),
            OBSIDIAN(Material.ROCK, SoundType.STONE, 22.5F, 3000.0F, 3);

            companion object {
                fun getSafely(ordinal: Int): ContainerMaterial {
                    return values()[if (ordinal >= 0) ordinal % values().size else 0]
                }

                fun getVariantStrings(prefix: String): Array<String> {
                    return values().map { "${prefix}_${it.getName()}" }.toTypedArray()
                }
            }
        }
    }
}
