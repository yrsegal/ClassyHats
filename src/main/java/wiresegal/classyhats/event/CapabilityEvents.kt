package wiresegal.classyhats.event

import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityList
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldServer
import net.minecraft.world.storage.loot.LootContext
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.*
import wiresegal.classyhats.ClassyHats
import wiresegal.classyhats.ClassyHatsConfig
import wiresegal.classyhats.ClassyHatsContent
import wiresegal.classyhats.capability.CapabilityHatContainer
import wiresegal.classyhats.capability.data.ClassyHatProvider
import wiresegal.classyhats.network.PacketEntityHatSync
import wiresegal.classyhats.network.PacketHatSync
import java.util.*

@Mod.EventBusSubscriber(modid = ClassyHats.ID)
object CapabilityEvents {


    @SubscribeEvent
    @JvmStatic
    fun cloneCapabilitiesEvent(event: PlayerEvent.Clone) = try {
        val bco = CapabilityHatContainer.getCapability(event.original)
        val nbt = bco.serializeNBT()
        val bcn = CapabilityHatContainer.getCapability(event.entityPlayer)
        bcn.deserializeNBT(nbt)
    } catch (e: Exception) {
        ClassyHats.LOGGER.error("Could not clone player [" + event.original.name + "] HATS when changing dimensions")
    }

    @SubscribeEvent
    @JvmStatic
    fun attachCapabilitiesPlayer(event: AttachCapabilitiesEvent<Entity>) {
        if (event.`object` is EntityPlayer)
            event.addCapability(ResourceLocation(ClassyHats.ID, "container"), ClassyHatProvider())
    }

    val customKey = "${ClassyHats.ID}:hat"

    @SubscribeEvent
    @JvmStatic
    fun onEntityJoinWorld(event: EntityJoinWorldEvent) {
        val customData = event.entity.entityData
        val world = event.world
        val name = EntityList.getKey(event.entity).toString()
        val entity = event.entity
        if (name in ClassyHatsConfig.names && world is WorldServer && entity is EntityLiving) {
            if (!customData.hasKey(customKey)) {
                val stack = world.lootTableManager.getLootTableFromLocation(ResourceLocation(ClassyHats.ID, "combined"))
                        .generateLootForPools(world.rand, LootContext(0f, world, world.lootTableManager, null, null, null))
                if (stack.isNotEmpty() && world.rand.nextFloat() < ClassyHatsConfig.hatSpawnPercentage)
                    customData.setString(customKey, ClassyHatsContent.HAT.getHat(stack[0]).name)
                else
                    customData.setString(customKey, "")
            }
            mapOfEntity.put(entity, customData.getString(customKey))
        }
    }

    private val mapOfEntity = WeakHashMap<EntityLiving, String>()

    @SubscribeEvent
    @JvmStatic
    fun onLivingUpdate(event: LivingEvent.LivingUpdateEvent) {
        val customData = event.entity.entityData
        val entity = event.entity
        val world = entity.world
        val name = EntityList.getKey(event.entity).toString()
        if (name in ClassyHatsConfig.names && world is WorldServer && entity is EntityLiving) {
            val hat = customData.getString(customKey)
            val prev = mapOfEntity[entity]
            if (hat != prev) {
                mapOfEntity.put(entity, hat)
                CapabilityHatContainer.getEntry(entity, world)?.trackingPlayers?.forEach {
                    PacketHandler.NETWORK.sendTo(PacketEntityHatSync(entity.entityId, hat), it)
                }
            }
        }
    }

    @SubscribeEvent
    @JvmStatic
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
    @JvmStatic
    fun onPlayerLoggedIn(event: PlayerLoggedInEvent) {
        syncDataFor(event.player, event.player as EntityPlayerMP)
    }

    @SubscribeEvent
    @JvmStatic
    fun onPlayerChangedDimension(event: PlayerChangedDimensionEvent) {
        syncDataFor(event.player, event.player as EntityPlayerMP)
    }

    @SubscribeEvent
    @JvmStatic
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        syncDataFor(event.player, event.player as EntityPlayerMP)
    }

    fun syncDataFor(entity: EntityPlayer, to: EntityPlayerMP) {
        val data = CapabilityHatContainer.getCapability(entity)
        val hats = if (entity.entityId == to.entityId) data.serializeNBT() else null
        PacketHandler.NETWORK.sendTo(PacketHatSync(entity.entityId, data.equipped, hats), to)
    }
}

