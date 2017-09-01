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
    var hats: List<ItemStack>

    var equipped: ItemStack

    fun addStoredHat(stack: ItemStack)

    fun removeStoredHat(stack: ItemStack)
}
