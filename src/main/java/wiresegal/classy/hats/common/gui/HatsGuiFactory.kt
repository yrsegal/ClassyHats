package wiresegal.classy.hats.common.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler


/**
 * @author WireSegal
 * Created at 4:26 PM on 9/1/17.
 */
object HatsGuiFactory : IGuiHandler {
    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World?, x: Int, y: Int, z: Int): Any? {
        if (ID == 1) return GuiHatBag(player)
        return GuiHat(player)
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        if (ID == 1) return ContainerHatBag(player.inventory, player)
        return ContainerHat(player.inventory, player)
    }
}
