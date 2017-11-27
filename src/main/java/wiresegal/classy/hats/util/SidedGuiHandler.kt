package wiresegal.classy.hats.util

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import wiresegal.classy.hats.capability.CapabilityHatContainer
import wiresegal.classy.hats.client.gui.GuiHatBag
import wiresegal.classy.hats.client.gui.GuiHatInventory
import wiresegal.classy.hats.container.ContainerHatBag
import wiresegal.classy.hats.container.ContainerHatInventory

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
