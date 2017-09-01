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

    val missingno = Hat("missingno", 0.0, true)
    val hats = mutableMapOf(
            "missingno" to missingno,
            "tophat" to Hat("tophat")
    )

    var rpl = File("")
        private set

    data class Hat(val name: String, val weight: Double = 1.0, val elusive: Boolean = false) {
        fun toJson() = json {
            obj(
                    "name" to name,
                    *(if (weight != 1.0) arrayOf("weight" to weight) else arrayOf()),
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
                blacklist.map { it.asString }.filter { it != "missingno" }.forEach { hats.remove(it) }

                val hats = if (load.has("custom_hats")) load.getAsJsonArray("custom_hats") else JsonArray()
                for (hat in hats) {
                    val hatObj = hat.asJsonObject
                    val name = hatObj.getAsJsonPrimitive("name").asString.toLowerCase(Locale.ROOT)
                    val weight = if (hatObj.has("weight")) hatObj.getAsJsonPrimitive("weight").asDouble else 1.0
                    val elusive = if (hatObj.has("elusive")) hatObj.getAsJsonPrimitive("elusive").asBoolean else false
                    newHats.put(name, Hat(name, weight, elusive))
                }
                this.hats.putAll(newHats)
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
                        "blacklist_hats" to array(),
                        "custom_hats" to array(Hat("example", 2.0, true).toJson())
                )
            }.serialize())
        }

        // A failsafe
        hats.put("missingno", missingno)

        rpl = File(e.modConfigurationDirectory, "classyhats_resources")
        rpl.mkdir()
    }
}
