package wiresegal.classyhats.event

import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import wiresegal.classyhats.ClassyHats
import wiresegal.classyhats.client.keybind.KeyBindHandler
import wiresegal.classyhats.network.PacketHatGuiOpen


@Mod.EventBusSubscriber(modid = ClassyHats.ID)
object GuiEvents {
    val SLOT_HAT = ResourceLocation(ClassyHats.ID, "gui/slot_hat")

    @SubscribeEvent
    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun onTextureStitchPre(event: TextureStitchEvent.Pre) {
        event.map.registerSprite(SLOT_HAT)
    }

    @SubscribeEvent
    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun onInputKey(event: InputEvent.KeyInputEvent){
        val mc = Minecraft.getMinecraft()
        if(mc.currentScreen == null){
            if(KeyBindHandler.KEY.isPressed)
                PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(0))
        }
    }
}