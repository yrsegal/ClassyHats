package wiresegal.classy.hats.common.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation


/**
 * @author WireSegal
 * Created at 4:09 PM on 9/1/17.
 */
class GuiHatBag(player: EntityPlayer) : GuiContainer(ContainerHatBag(player.inventory, player)) {

    /** The old x position of the mouse pointer  */
    private var oldMouseX: Float = 0.toFloat()
    /** The old y position of the mouse pointer  */
    private var oldMouseY: Float = 0.toFloat()

    init {
        this.allowUserInput = true
        xSize = 252
        ySize = 193
    }

    /**
     * Draws the screen and all the components in it.
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.oldMouseX = mouseX.toFloat()
        this.oldMouseY = mouseY.toFloat()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun drawGuiContainerBackgroundLayer(partTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.textureManager.bindTexture(background)
        val k = this.guiLeft
        val l = this.guiTop
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize)

        GuiInventory.drawEntityOnScreen(k + 25 + 196, l + 83, 30, (k + 25 + 196).toFloat() - this.oldMouseX, (l + 83 - 50).toFloat() - this.oldMouseY, this.mc.player)
    }

    companion object {
        val background = ResourceLocation("classyhats", "textures/gui/hat_bag.png")
    }
}
