package wiresegal.classyhats.client.keybind

import com.teamwizardry.librarianlib.core.LibrarianLib
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard.KEY_H
import wiresegal.classyhats.ClassyHats

@SideOnly(Side.CLIENT)
object KeyBindHandler {
    val KEY = KeyBinding(LibrarianLib.PROXY.translate(
            "keybind.${ClassyHats.ID}.hats_inventory"
    ), KEY_H, "key.categories.inventory")


}
