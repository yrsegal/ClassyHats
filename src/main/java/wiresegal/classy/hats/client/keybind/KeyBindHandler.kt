package wiresegal.classy.hats.client.keybind

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard.KEY_H
import wiresegal.classy.hats.ClassyHats
import wiresegal.classy.hats.network.PacketHatGuiOpen

@SideOnly(Side.CLIENT)
object KeyBindHandler {
    val KEY = KeyBinding(LibrarianLib.PROXY.translate(
            "keybind.${ClassyHats.ID}.hats_inventory"
    ), KEY_H, "key.categories.inventory")

    @SubscribeEvent
    fun onInputKey(event: InputEvent.KeyInputEvent){
        val mc = Minecraft.getMinecraft()
        if(mc.currentScreen == null){
            if(KEY.isPressed)
                PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(0))
        }
    }
}
