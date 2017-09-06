package wiresegal.classy.hats.common.gui

import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import wiresegal.classy.hats.LibMisc


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
        drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize)
        if (mouseX <= k + 192 + 8 && mouseX >= k + 192 && mouseY <= l + 143 + 8 && mouseY >= l + 143)
            drawTexturedModalRect(k + 192, l + 143, 0, 240, 8, 8)
        else
            drawTexturedModalRect(k + 192, l + 143, 0, 248, 8, 8)

        GuiInventory.drawEntityOnScreen(k + 25 + 196, l + 83, 30, (k + 25 + 196).toFloat() - this.oldMouseX, (l + 83 - 50).toFloat() - this.oldMouseY, this.mc.player)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        val k = this.guiLeft
        val l = this.guiTop
        if (mouseButton == 0 && mouseX <= k + 192 + 8 && mouseX >= k + 192 && mouseY <= l + 143 + 8 && mouseY >= l + 143)
            PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(0))
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        val k = this.guiLeft
        val l = this.guiTop
        if (mouseX <= k + 192 + 8 && mouseX >= k + 192 && mouseY <= l + 143 + 8 && mouseY >= l + 143)
            drawHoveringText(I18n.format("${LibMisc.MOD_ID}.misc.back_to_hat"), mouseX - k, mouseY - l)
    }

    companion object {
        val background = ResourceLocation("classyhats", "textures/gui/hat_bag.png")
    }
}
