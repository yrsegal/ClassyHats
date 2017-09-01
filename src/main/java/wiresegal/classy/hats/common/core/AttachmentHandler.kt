package wiresegal.classy.hats.common.core

import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.*
import wiresegal.classy.hats.ClassyHats
import wiresegal.classy.hats.LibMisc
import wiresegal.classy.hats.common.hat.CapabilityHat
import wiresegal.classy.hats.common.hat.HatStorageProvider




/**
 * @author WireSegal
 * Created at 11:57 AM on 9/1/17.
 */
object AttachmentHandler {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun getCapability(player: EntityPlayer)
            = player.getCapability(CapabilityHat.CAPABILITY_HAT, null)!!

    @SubscribeEvent
    fun cloneCapabilitiesEvent(event: PlayerEvent.Clone) {
        try {
            val bco = getCapability(event.original)
            val nbt = bco.serializeNBT()
            val bcn = getCapability(event.entityPlayer)
            bcn.deserializeNBT(nbt)
        } catch (e: Exception) {
            ClassyHats.LOGGER.error("Could not clone player [" + event.original.name + "] hats when changing dimensions")
        }

    }

    @SubscribeEvent
    fun attachCapabilitiesPlayer(event: AttachCapabilitiesEvent<Entity>) {
        if (event.`object` is EntityPlayer)
            event.addCapability(ResourceLocation(LibMisc.MOD_ID, "container"), HatStorageProvider())
    }


    @SubscribeEvent
    fun onPlayerStartTracking(event: PlayerEvent.StartTracking) {
        if (event.target is EntityPlayer)
            syncDataFor(event.target as EntityPlayer, event.entityPlayer as EntityPlayerMP)
    }

    @SubscribeEvent
    fun onPlayerLogin(event: PlayerLoggedInEvent) {
        syncDataFor(event.player, event.player as EntityPlayerMP)
    }

    @SubscribeEvent
    fun onPlayerChangeDimension(event: PlayerChangedDimensionEvent) {
        syncDataFor(event.player, event.player as EntityPlayerMP)
    }

    @SubscribeEvent
    fun onPlayerSpawn(event: PlayerRespawnEvent) {
        syncDataFor(event.player, event.player as EntityPlayerMP)
    }

    fun syncDataFor(entity: EntityPlayer, to: EntityPlayerMP) {
        val data = getCapability(entity)
        val hat = data.equipped
        val hats = if (entity.entityId == to.entityId) data.hats else null

        PacketHandler.NETWORK.sendTo(PacketHatSync(hat, hats), to)
    }
}

