package wiresegal.classy.hats.client.keybind

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
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
    fun onPlayerTick(event: PlayerTickEvent) {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.START) {
            if (KEY.isPressed && FMLClientHandler.instance().client.inGameHasFocus) {
                PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(0))
            }
        }
    }
}
