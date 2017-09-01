package wiresegal.classy.hats.common.hat

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
 * @author WireSegal
 * Created at 8:35 PM on 8/31/17.
 */
class BaseHatStorage : IHatStorage {

    override val hats: List<ItemStack>
        get() = throw UnsupportedOperationException()
    override val equipped: ItemStack
        get() = throw UnsupportedOperationException()

    override fun equipHat(stack: ItemStack): Boolean {
        throw UnsupportedOperationException()
    }

    override fun addStoredHat(stack: ItemStack): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeStoredHat(stack: ItemStack): Boolean {
        throw UnsupportedOperationException()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        throw UnsupportedOperationException()
    }

    override fun serializeNBT(): NBTTagCompound {
        throw UnsupportedOperationException()
    }
}
