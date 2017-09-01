package wiresegal.classy.hats.common.hat

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.entity.EntityTracker
import net.minecraft.entity.EntityTrackerEntry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.IntHashMap
import net.minecraftforge.items.ItemStackHandler
import wiresegal.classy.hats.common.core.AttachmentHandler

/**
 * @author WireSegal
 * Created at 8:35 PM on 8/31/17.
 */
class BaseHatStorage : IHatStorage {

    override var player: EntityPlayer? = null

    override val hats: ItemStackHandler = ItemStackHandler(50)

    override var equipped: ItemStack = ItemStack.EMPTY
        set(value) {
            field = value
            val pl = player
            if (pl is EntityPlayerMP) {
                val entry = getEntry(pl)
                if (entry != null) for (player in entry.trackingPlayers)
                    AttachmentHandler.syncDataFor(pl, player)
            }
        }

    companion object {

        val handle = MethodHandleHelper.wrapperForGetter(EntityTracker::class.java, "field_72794_c", "trackedEntityHashTable")

        private fun getEntry(player: EntityPlayerMP): EntityTrackerEntry? {
            return (handle(player.serverWorld.entityTracker) as IntHashMap<*>).lookup(player.entityId) as? EntityTrackerEntry
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        equipped = ItemStack(nbt.getCompoundTag("Equipped"))
        hats.deserializeNBT(nbt)
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = hats.serializeNBT()
        nbt.setTag("Equipped", equipped.writeToNBT(NBTTagCompound()))
        return nbt
    }
}
