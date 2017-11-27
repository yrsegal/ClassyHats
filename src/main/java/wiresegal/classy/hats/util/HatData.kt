package wiresegal.classy.hats.util

import com.google.gson.JsonElement
import com.teamwizardry.librarianlib.features.kotlin.json

data class HatData(val name: String, val weight: Int = -1, val elusive: Boolean = false) {
    constructor(name: String, weight: Double, elusive: Boolean = false)
            : this(name, (weight * 40).toInt(), elusive)

    fun toJson(): JsonElement {
        val weight = if (weight != -1) arrayOf("weight" to weight) else arrayOf()
        val elusive = if (elusive) arrayOf("elusive" to elusive) else arrayOf()
        return json { obj("name" to name, *weight, *elusive) }
    }
}