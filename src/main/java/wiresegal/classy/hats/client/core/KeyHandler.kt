package wiresegal.classy.hats.client.core

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard.KEY_H
import wiresegal.classy.hats.common.gui.PacketHatGuiOpen


/**
 * @author WireSegal
 * Created at 4:33 PM on 9/1/17.
 */
@SideOnly(Side.CLIENT)
object KeyHandler {

    var key = KeyBinding(LibrarianLib.PROXY.translate("keybind.hats_inventory"),
            KEY_H, "key.categories.inventory")

    init {
        ClientRegistry.registerKeyBinding(key)

        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun playerTick(event: PlayerTickEvent) {
        if (event.side == Side.SERVER) return
        if (event.phase == TickEvent.Phase.START) {
            if (key.isPressed && FMLClientHandler.instance().client.inGameHasFocus) {
                PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(0))
            }
        }
    }
}
