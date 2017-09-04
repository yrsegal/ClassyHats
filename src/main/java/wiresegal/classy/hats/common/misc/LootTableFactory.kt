package wiresegal.classy.hats.common.misc

import net.minecraft.util.ResourceLocation
import net.minecraft.world.storage.loot.*
import net.minecraft.world.storage.loot.functions.SetNBT
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.LootTableLoadEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import wiresegal.classy.hats.LibMisc
import wiresegal.classy.hats.common.core.HatConfigHandler
import wiresegal.classy.hats.common.hat.ItemHat

/**
 * @author WireSegal
 * Created at 9:43 PM on 9/3/17.
 */
object LootTableFactory {

    private val elusiveTable: LootTable
    private val regularTable: LootTable

    init {
        val hats = HatConfigHandler.hats.values.filter { it != HatConfigHandler.missingno }

        val elusive = hats.filter { it.elusive }
        val regular = hats.filterNot { it.elusive }

        val elusiveEntries = elusive.map { LootEntryItem(ItemHat, it.weight, 0,
                arrayOf(SetNBT(arrayOf(), ItemHat.ofHat(it).tagCompound)), arrayOf(), it.name) }.toTypedArray()
        val regularEntries = regular.map { LootEntryItem(ItemHat, it.weight, 0,
                arrayOf(SetNBT(arrayOf(), ItemHat.ofHat(it).tagCompound)), arrayOf(), it.name) }.toTypedArray()

        val elusivePool = LootPool(elusiveEntries, arrayOf(), RandomValueRange(1f), RandomValueRange(0f), "elusive")
        val regularPool = LootPool(regularEntries, arrayOf(), RandomValueRange(1f), RandomValueRange(0f), "regular")

        elusiveTable = LootTable(arrayOf(elusivePool))
        regularTable = LootTable(arrayOf(regularPool))

        MinecraftForge.EVENT_BUS.register(this)

        LootTableList.register(ResourceLocation(LibMisc.MOD_ID, "elusive"))
        LootTableList.register(ResourceLocation(LibMisc.MOD_ID, "regular"))
        LootTableList.register(ResourceLocation(LibMisc.MOD_ID, "combined"))
    }

    @SubscribeEvent
    fun onLootTable(e: LootTableLoadEvent) {
        if (e.name.resourceDomain == LibMisc.MOD_ID) {
            if (e.name.resourcePath == "elusive")
                e.table = elusiveTable
            else if (e.name.resourcePath == "regular")
                e.table = regularTable
        }
    }


}
