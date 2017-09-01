package wiresegal.classy.hats.common.hat

import com.teamwizardry.librarianlib.features.helpers.nonnullListOf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants

/**
 * @author WireSegal
 * Created at 8:35 PM on 8/31/17.
 */
class BaseHatStorage : IHatStorage {

    private var hatsInternal = nonnullListOf<ItemStack>()

    override var hats: List<ItemStack>
        get() = hatsInternal
        set(value) {
            hatsInternal.clear()
            hatsInternal.addAll(value)
        }

    override var equipped: ItemStack = ItemStack.EMPTY

    override fun addStoredHat(stack: ItemStack) {
        val hat = ItemHat.getHat(stack)
        if (!stack.isEmpty && hats.none { ItemHat.getHat(it) == hat })
            hatsInternal.add(stack)
    }

    override fun removeStoredHat(stack: ItemStack) {
        val stackHat = ItemHat.getHat(stack)
        hats
                .filter { ItemHat.getHat(it) == stackHat }
                .firstOrNull()
                ?.let { hatsInternal.remove(it) }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        val tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND)
        hats = List(tagList.tagCount()) {
            val itemTag = tagList.getCompoundTagAt(it)
            ItemStack(itemTag)
        }
        equipped = ItemStack(nbt.getCompoundTag("Equipped"))
    }

    override fun serializeNBT(): NBTTagCompound {
        val itemList = NBTTagList()
        hatsInternal.indices
                .filterNot { hatsInternal[it].isEmpty }
                .forEach { itemList.appendTag(hatsInternal[it].writeToNBT(NBTTagCompound())) }
        val nbt = NBTTagCompound()
        nbt.setTag("Items", itemList)
        nbt.setTag("Equipped", equipped.writeToNBT(NBTTagCompound()))
        return nbt
    }
}
