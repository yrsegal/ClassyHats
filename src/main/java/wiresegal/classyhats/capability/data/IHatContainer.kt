package wiresegal.classyhats.capability.data

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.items.ItemStackHandler

/**
 * @author WireSegal
 * Created at 8:34 PM on 8/31/17.
 *
 * A capability that marks an item as a hat.
 */
interface IHatContainer : INBTSerializable<NBTTagCompound> {
    val hats: ItemStackHandler

    var player: EntityPlayer?

    var equipped: ItemStack

    var currentHatSection: Int
}
