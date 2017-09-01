package wiresegal.classy.hats.common.hat

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

/**
 * @author WireSegal
 * Created at 8:34 PM on 8/31/17.
 *
 * A capability that marks an item as a hat.
 */
interface IHatStorage : INBTSerializable<NBTTagCompound> {
    val hats: List<ItemStack>

    val equipped: ItemStack

    fun equipHat(stack: ItemStack): Boolean

    fun addStoredHat(stack: ItemStack): Boolean

    fun removeStoredHat(stack: ItemStack): Boolean
}
