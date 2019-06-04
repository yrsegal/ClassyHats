package wiresegal.classyhats.util

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityList
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldServer
import net.minecraft.world.storage.loot.*
import net.minecraft.world.storage.loot.LootTableList.*
import net.minecraft.world.storage.loot.functions.SetNBT
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.event.LootTableLoadEvent
import net.minecraftforge.event.entity.living.LivingDropsEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import wiresegal.classyhats.ClassyHats
import wiresegal.classyhats.ClassyHatsConfig
import wiresegal.classyhats.ClassyHatsContent
import wiresegal.classyhats.event.CapabilityEvents


/**
 * @author WireSegal
 * Created at 9:43 PM on 9/3/17.
 */
@Mod.EventBusSubscriber(modid = ClassyHats.ID)
object LootTableFactory {
    private val elusiveTable: LootTable
    private val regularTable: LootTable
    private val pool: LootPool

    init {
        val hats = ClassyHatsConfig.hats.values.filter { it != ClassyHatsConfig.missingno }

        val elusive = hats.filter { it.elusive }
        val regular = hats.filterNot { it.elusive }

        val elusiveEntries = elusive.map {
            LootEntryItem(ClassyHatsContent.HAT, if (it.weight == -1) 40 else it.weight, 0,
                    arrayOf(SetNBT(arrayOf(), ClassyHatsContent.HAT.ofHat(it).tagCompound)), arrayOf(), it.name)
        }.toTypedArray()
        val regularEntries = regular.map {
            LootEntryItem(ClassyHatsContent.HAT, if (it.weight == -1) 40 else it.weight, 0,
                    arrayOf(SetNBT(arrayOf(), ClassyHatsContent.HAT.ofHat(it).tagCompound)), arrayOf(), it.name)
        }.toTypedArray()

        val elusivePool = LootPool(elusiveEntries, arrayOf(), RandomValueRange(1f), RandomValueRange(0f), "elusive")
        val regularPool = LootPool(regularEntries, arrayOf(), RandomValueRange(1f), RandomValueRange(0f), "regular")

        elusiveTable = LootTable(arrayOf(elusivePool))
        regularTable = LootTable(arrayOf(regularPool))


        LootTableList.register(ResourceLocation(ClassyHats.ID, "elusive"))
        LootTableList.register(ResourceLocation(ClassyHats.ID, "regular"))
        LootTableList.register(ResourceLocation(ClassyHats.ID, "combined"))

        pool = LootPool(arrayOf(LootEntryTable(ResourceLocation(ClassyHats.ID, "combined"), 1, 0, arrayOf(), ClassyHats.ID)),
                arrayOf(),
                RandomValueRange(1f, 3f),
                RandomValueRange(0f),
                ClassyHats.ID)
    }

    @SubscribeEvent
    @JvmStatic
    fun onLootTable(e: LootTableLoadEvent) {
        if (e.name.namespace == ClassyHats.ID) {
            if (e.name.path == "elusive")
                e.table = elusiveTable
            else if (e.name.path == "regular")
                e.table = regularTable
        } else if (ClassyHatsConfig.shouldInjectLootChests && (e.name == CHESTS_SIMPLE_DUNGEON ||
                e.name == CHESTS_STRONGHOLD_LIBRARY ||
                e.name == CHESTS_END_CITY_TREASURE ||
                e.name == CHESTS_IGLOO_CHEST ||
                e.name == CHESTS_WOODLAND_MANSION ||
                e.name == CHESTS_DESERT_PYRAMID ||
                e.name == CHESTS_JUNGLE_TEMPLE ||
                e.name == GAMEPLAY_FISHING_TREASURE))
            e.table.addPool(pool)
    }

    private val FAKE_PLAYER_PATTERN = "^(?:\\[.*])|(?:ComputerCraft)$".toRegex()
    fun isTruePlayer(e: Entity)
            = e !is EntityPlayer || e !is FakePlayer && !FAKE_PLAYER_PATTERN.matches(e.name)

    @SubscribeEvent
    fun onBossDeath(e: LivingDropsEvent) {
        if (!ClassyHatsConfig.shouldInjectLootBoss) return

        val boss = !e.entityLiving.isNonBoss

        val entity = e.entityLiving
        val world = entity.world

        if (world is WorldServer && e.isRecentlyHit) {
            if (boss) {
                val killer = e.source.trueSource as? EntityPlayer
                if (killer != null && isTruePlayer(killer)) {
                    val item = mutableListOf<ItemStack>()
                    val context = LootContext(0f, world, world.lootTableManager, entity, killer, e.source)
                    pool.generateLoot(item, world.rand, context)
                    if (item.size == 1)
                        item.addAll(elusiveTable.generateLootForPools(world.rand, context))

                    for (i in item) {
                        val entityItem = EntityItem(world, entity.posX, entity.posY, entity.posZ, i)
                        entityItem.setDefaultPickupDelay()
                        e.drops.add(entityItem)
                    }
                }
            } else {
                if (EntityList.getKey(entity).toString() in ClassyHatsConfig.names) {
                    val data = entity.entityData
                    if (data.hasKey(CapabilityEvents.customKey)) {
                        val str = data.getString(CapabilityEvents.customKey)
                        if (str.isNotEmpty()) {
                            val entityItem = EntityItem(world, entity.posX, entity.posY, entity.posZ, ClassyHatsContent.HAT.ofHat(str))
                            entityItem.setDefaultPickupDelay()
                            e.drops.add(entityItem)
                        }
                    }
                }
            }
        }
    }
}
