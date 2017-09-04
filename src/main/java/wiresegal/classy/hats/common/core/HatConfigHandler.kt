package wiresegal.classy.hats.common.core

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.kotlin.serialize
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import wiresegal.classy.hats.ClassyHats
import java.io.File
import java.util.*

/**
 * @author WireSegal
 * Created at 8:48 PM on 8/31/17.
 */
object HatConfigHandler {

    val missingno = Hat("missingno", 0, true)
    val hats = mutableMapOf(
            "missingno" to missingno
    ).apply { defaultHats.forEach { this.put(it.name, it) } }

    var rpl = File("")
        private set

    var shouldInjectLootChests = true
    var shouldInjectLootBoss = true

    data class Hat(val name: String, var weight: Int = -1, val elusive: Boolean = false) {
        fun toJson() = json {
            obj(
                    "name" to name,
                    *(if (weight != -1) arrayOf("weight" to weight) else arrayOf()),
                    *(if (elusive) arrayOf("elusive" to elusive) else arrayOf())
            )
        }
    }

    fun loadPreInit(e: FMLPreInitializationEvent) {
        val path = File(e.modConfigurationDirectory, "classyhats.json")
        var succeeded = false
        if (path.exists()) {
            try {
                val newHats = mutableMapOf<String, Hat>()
                val load = JsonParser().parse(path.reader()).asJsonObject

                val blacklist = if (load.has("blacklist_hats")) load.getAsJsonArray("blacklist_hats") else JsonArray()
                blacklist.map { it.asString.toLowerCase(Locale.ROOT) }.filter { it != "missingno" }.forEach { hats.remove(it) }


                shouldInjectLootChests = if (load.has("loot_hat_from_chests")) load.getAsJsonPrimitive("loot_hat_from_chests").asBoolean else true
                shouldInjectLootBoss = if (load.has("loot_hat_from_boss")) load.getAsJsonPrimitive("loot_hat_from_boss").asBoolean else true

                val hats = if (load.has("custom_hats")) load.getAsJsonArray("custom_hats") else JsonArray()
                for (hat in hats) {
                    val hatObj = hat.asJsonObject
                    val name = hatObj.getAsJsonPrimitive("name").asString.toLowerCase(Locale.ROOT)
                    val weight = if (hatObj.has("weight")) hatObj.getAsJsonPrimitive("weight").asInt else -1
                    val elusive = if (hatObj.has("elusive")) hatObj.getAsJsonPrimitive("elusive").asBoolean else false
                    newHats.put(name, Hat(name, weight, elusive))
                }
                this.hats.putAll(newHats)

                this.hats.values.forEach { if (it != missingno && it.weight == -1) {
                    it.weight = 40
                }}

                succeeded = true
            } catch (e: Exception) {
                ClassyHats.LOGGER.error("Failed to parse hat config $path")
            }
        }

        if (!succeeded) {
            path.writeText(json {
                obj(
                        "__comment0" to "The folder classyhats_resources will act as a resource pack for any custom hats you want to add.",
                        "__comment1" to "Put any hat names from the default hats you don't want in the blacklist.",
                        "__default_weight" to 40,
                        "__example_custom_hat" to array(Hat("example", 2, true).toJson()),
                        "blacklist_hats" to array(),
                        "loot_hat_from_chests" to true,
                        "loot_hat_from_boss" to true,
                        "custom_hats" to array()
                )
            }.serialize())
        }

        // A failsafe
        hats.put("missingno", missingno)

        rpl = File(e.modConfigurationDirectory, "classyhats_resources")
        rpl.mkdir()
    }
}
