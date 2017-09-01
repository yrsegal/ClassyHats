package wiresegal.classy.hats.common.core

import com.google.gson.JsonParser
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.kotlin.serialize
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import wiresegal.classy.hats.ClassyHats
import java.io.File

/**
 * @author WireSegal
 * Created at 8:48 PM on 8/31/17.
 */
object HatConfigHandler {

    val missingno = Hat("missingno", 0.0, true)
    var hats = mutableMapOf(
            "tophat" to Hat("tophat")
    )
        private set

    var rpl = File("")
        private set

    data class Hat(val name: String, val weight: Double = 1.0, val elusive: Boolean = false) {
        fun toJson() = json {
            obj(
                    "name" to name,
                    "weight" to weight,
                    "elusive" to elusive
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
                val hats = load.getAsJsonArray("hats")
                for (hat in hats) {
                    val hatObj = hat.asJsonObject
                    val name = hatObj.getAsJsonPrimitive("name").asString
                    val weight = if (hatObj.has("weight")) hatObj.getAsJsonPrimitive("weight").asDouble else 1.0
                    val elusive = if (hatObj.has("elusive")) hatObj.getAsJsonPrimitive("elusive").asBoolean else false
                    newHats.put(name, Hat(name, weight, elusive))
                }
                this.hats = newHats
                succeeded = true
            } catch (e: Exception) {
                ClassyHats.LOGGER.error("Failed to parse hat config $path")
            }
        }

        if (!succeeded) {
            path.writeText(json {
                obj(
                        "__comment" to "The folder classyhats_resources will act as a resource pack for any custom hats you want to add.",
                        "hats" to array(*hats.values.map { it.toJson() }.toTypedArray())
                )
            }.serialize())
        }

        hats.put("missingno", missingno)

        rpl = File(e.modConfigurationDirectory, "classyhats_resources")
        rpl.mkdir()
    }
}
