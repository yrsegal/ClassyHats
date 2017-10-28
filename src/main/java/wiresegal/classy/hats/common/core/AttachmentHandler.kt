package wiresegal.classy.hats.common.core

import com.teamwizardry.librarianlib.features.helpers.nonnullListOf
import com.teamwizardry.librarianlib.features.kotlin.get
import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityList
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldServer
import net.minecraft.world.storage.loot.LootContext
import net.minecraft.world.storage.loot.LootTableManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.*
import wiresegal.classy.hats.ClassyHats
import wiresegal.classy.hats.LibMisc
import wiresegal.classy.hats.common.hat.CapabilityHat
import wiresegal.classy.hats.common.hat.HatStorageProvider
import wiresegal.classy.hats.common.hat.ItemHat


/**
 * @author WireSegal
 * Created at 11:57 AM on 9/1/17.
 */
object AttachmentHandler {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun getCapability(player: EntityPlayer)
            = player.getCapability(CapabilityHat.CAPABILITY_HAT, null)!!.apply { this.player = player }

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

    val customKey = "${LibMisc.MOD_ID}:hat"

    @SubscribeEvent
    fun onEntityExist(event: EntityJoinWorldEvent) {
        val customData = event.entity.entityData
        val world = event.world
        val name = EntityList.getKey(event.entity).toString()
        if (name in HatConfigHandler.names && !customData.hasKey(customKey) && world is WorldServer && event.entity is EntityLiving) {
            val stack = world.lootTableManager.getLootTableFromLocation(ResourceLocation(LibMisc.MOD_ID, "combined"))
                    .generateLootForPools(world.rand, LootContext(0f, world, world.lootTableManager, null, null, null))
            if (stack.isNotEmpty() && world.rand.nextFloat() < HatConfigHandler.hatSpawnPercentage)
                customData.setString(customKey, ItemHat.getHat(stack[0]).name)
            else
                customData.setString(customKey, "")
        }
    }


    @SubscribeEvent
    fun onPlayerStartTracking(event: PlayerEvent.StartTracking) {
        if (event.target is EntityPlayer)
            syncDataFor(event.target as EntityPlayer, event.entityPlayer as EntityPlayerMP)
        else {
            val data = event.target.entityData
            if (data.hasKey(customKey))
                PacketHandler.NETWORK.sendTo(PacketEntityHatSync(event.target.entityId, data.getString(customKey)), event.entityPlayer as EntityPlayerMP)
        }
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
        val hats = if (entity.entityId == to.entityId) data.serializeNBT() else null
        PacketHandler.NETWORK.sendTo(PacketHatSync(entity.entityId, hat, hats), to)
    }
}

