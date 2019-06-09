package wiresegal.classyhats.util

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import wiresegal.classyhats.capability.CapabilityHatContainer
import wiresegal.classyhats.client.gui.GuiHatBag
import wiresegal.classyhats.client.gui.GuiHatInventory
import wiresegal.classyhats.container.ContainerHatBag
import wiresegal.classyhats.container.ContainerHatInventory

object SidedGuiHandler : IGuiHandler {
    override fun getClientGuiElement(id: Int, player: EntityPlayer, world: World?, x: Int, y: Int, z: Int): Any? {
        when (id) {
            0 -> return GuiHatInventory(player)
            1 -> return GuiHatBag(player)
        }
        return null
    }

    override fun getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        when (id) {
            0 -> return ContainerHatInventory(player.inventory, player)
            1 -> {
                val capability = CapabilityHatContainer.getCapability(player)
                return ContainerHatBag(player.inventory, player, capability.currentHatSection)
            }
        }
        return null
    }
}
