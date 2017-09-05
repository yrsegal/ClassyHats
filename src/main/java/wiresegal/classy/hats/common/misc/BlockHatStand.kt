package wiresegal.classy.hats.common.misc

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.EnumStringSerializable
import com.teamwizardry.librarianlib.features.base.block.TileMod
import com.teamwizardry.librarianlib.features.base.block.module.ModuleInventory
import com.teamwizardry.librarianlib.features.saving.Module
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
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
                return if (stack.item == ItemHat) super.getStackLimit(slot, stack) else 0
            }

            override fun onContentsChanged(slot: Int) {
                markDirty()
            }
        })
    }
}
