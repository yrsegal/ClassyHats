package wiresegal.classy.hats.common.gui

import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.InventoryEffectRenderer
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import wiresegal.classy.hats.LibMisc


/**
 * @author WireSegal
 * Created at 4:09 PM on 9/1/17.
 */
class GuiHat(player: EntityPlayer) : InventoryEffectRenderer(ContainerHat(player.inventory, player)) {

    /** The old x position of the mouse pointer  */
    private var oldMouseX: Float = 0.toFloat()
    /** The old y position of the mouse pointer  */
    private var oldMouseY: Float = 0.toFloat()

    init {
        this.allowUserInput = true
    }

    private fun resetGuiLeft() {
        this.guiLeft = (this.width - this.xSize) / 2
    }

    /**
     * Called from the main game loop to update the screen.
     */
    override fun updateScreen() {
        updateActivePotionEffects()
        resetGuiLeft()
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    override fun initGui() {
        this.buttonList.clear()
        super.initGui()
        resetGuiLeft()
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        this.fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 0x404040)
        val k = this.guiLeft
        val l = this.guiTop
        if (mouseX <= k + 81 + 8 && mouseX >= k + 81 && mouseY <= l + 39 + 8 && mouseY >= l + 39)
            drawHoveringText(I18n.format("${LibMisc.MOD_ID}.misc.back_to_bag"), mouseX - k, mouseY - l)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        val k = this.guiLeft
        val l = this.guiTop
        if (mouseButton == 0 && mouseX <= k + 81 + 8 && mouseX >= k + 81 && mouseY <= l + 39 + 8 && mouseY >= l + 39)
            PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(1))
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
        if (mouseX <= k + 81 + 8 && mouseX >= k + 81 && mouseY <= l + 39 + 8 && mouseY >= l + 39)
            drawTexturedModalRect(k + 81, l + 39, 8, 240, 8, 8)
        else
            drawTexturedModalRect(k + 81, l + 39, 8, 248, 8, 8)

        GuiInventory.drawEntityOnScreen(k + 51, l + 75, 30, (k + 51).toFloat() - this.oldMouseX, (l + 75 - 50).toFloat() - this.oldMouseY, this.mc.player)
    }

    companion object {

        val background = ResourceLocation("classyhats", "textures/gui/hat_inv.png")
    }
}
