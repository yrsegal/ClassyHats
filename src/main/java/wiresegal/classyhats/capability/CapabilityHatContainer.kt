package wiresegal.classyhats.capability

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityTracker
import net.minecraft.entity.EntityTrackerEntry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.IntHashMap
import net.minecraft.world.WorldServer
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.items.ItemStackHandler
import wiresegal.classyhats.capability.data.IHatContainer
import wiresegal.classyhats.event.CapabilityEvents

/**
 * @author WireSegal
 * Created at 8:35 PM on 8/31/17.
 */
class CapabilityHatContainer : IHatContainer {
    override val hats: ItemStackHandler = ItemStackHandler(HAT_SIZE)

    override var player: EntityPlayer? = null

    override var equipped: ItemStack = ItemStack.EMPTY
        set(value) {
            field = value
            val pl = player
            if (pl is EntityPlayerMP) {
                val entry = getEntry(pl, pl.serverWorld)
                if (entry != null) for (player in entry.trackingPlayers)
                    CapabilityEvents.syncDataFor(pl, player)
            }
        }

    override var currentHatSection: Int = 0

    override fun deserializeNBT(nbt: NBTTagCompound) {
        equipped = ItemStack(nbt.getCompoundTag("Equipped"))
        hats.deserializeNBT(nbt)
        if (hats.slots != HAT_SIZE)
            hats.setSize(HAT_SIZE)
        currentHatSection = nbt.getInteger("Section")
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = hats.serializeNBT()
        nbt.setTag("Equipped", equipped.writeToNBT(NBTTagCompound()))
        nbt.setInteger("Section", currentHatSection)
        return nbt
    }

    companion object {
        @CapabilityInject(IHatContainer::class)
        lateinit var CAPABILITY: Capability<IHatContainer>

        const val HAT_SIZE = 1000

        private val handle = MethodHandleHelper.wrapperForGetter(
                EntityTracker::class.java, "field_72794_c", "trackedEntityHashTable"
        )

        fun getCapability(player: EntityPlayer) = player.getCapability(CapabilityHatContainer.CAPABILITY, null)!!.apply { this.player = player }

        fun getEntry(player: Entity, worldServer: WorldServer): EntityTrackerEntry? =
                (handle(worldServer.entityTracker) as IntHashMap<*>)
                        .lookup(player.entityId) as? EntityTrackerEntry
    }
}
