package wiresegal.classy.hats.event

import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import wiresegal.classy.hats.ClassyHats

object GuiTextureEvents {
    val SLOT_HAT = ResourceLocation(ClassyHats.ID, "gui/slot_hat")

    @SubscribeEvent
    fun onTextureStitchPre(event: TextureStitchEvent.Pre) {
        event.map.registerSprite(SLOT_HAT)
    }
}