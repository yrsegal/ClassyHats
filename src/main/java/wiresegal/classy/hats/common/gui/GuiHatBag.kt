package wiresegal.classy.hats.common.gui

import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.util.ResourceLocation
import wiresegal.classy.hats.LibMisc
import wiresegal.classy.hats.common.hat.BaseHatStorage


/**
 * @author WireSegal
 * Created at 4:09 PM on 9/1/17.
 */
class GuiHatBag(player: EntityPlayer, private var slotPos: Int) : GuiContainer(ContainerHatBag(player.inventory, player, slotPos)) {

    /** The old x position of the mouse pointer  */
    private var oldMouseX: Float = 0.toFloat()
    /** The old y position of the mouse pointer  */
    private var oldMouseY: Float = 0.toFloat()

    val BAG_BUTTON_X = 192
    val BAG_BUTTON_Y = 143

    val ARROW_UP_BUTTON_X = 194
    val ARROW_UP_BUTTON_Y = 3

    val ARROW_DOWN_BUTTON_X = 194
    val ARROW_DOWN_BUTTON_Y = 95

    private val totalSlots = BaseHatStorage.HAT_SIZE / 50 - 1

    init {
        this.allowUserInput = true
        xSize = 252
        ySize = 193

        slotPos = Math.max(0, Math.min(totalSlots, slotPos))
    }

    /**
     * Draws the screen and all the components in it.
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.oldMouseX = mouseX.toFloat()
        this.oldMouseY = mouseY.toFloat()
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(partTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.textureManager.bindTexture(background)
        val k = this.guiLeft
        val l = this.guiTop
        drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize)

        if (mouseX <= k + BAG_BUTTON_X + 8 && mouseX >= k + BAG_BUTTON_X && mouseY <= l + BAG_BUTTON_Y + 8 && mouseY >= l + BAG_BUTTON_Y)
            drawTexturedModalRect(k + BAG_BUTTON_X, l + BAG_BUTTON_Y, 0, 240, 8, 8)
        else
            drawTexturedModalRect(k + BAG_BUTTON_X, l + BAG_BUTTON_Y, 0, 248, 8, 8)

        if ((slotPos != 0) && mouseX <= k + ARROW_UP_BUTTON_X + 8 && mouseX >= k + ARROW_UP_BUTTON_X && mouseY <= l + ARROW_UP_BUTTON_Y + 8 && mouseY >= l + ARROW_UP_BUTTON_Y)
            drawTexturedModalRect(k + ARROW_UP_BUTTON_X, l + ARROW_UP_BUTTON_Y, 16, 240, 8, 8)
        else
            drawTexturedModalRect(k + ARROW_UP_BUTTON_X, l + ARROW_UP_BUTTON_Y, 24, 240, 8, 8)

        if ((slotPos != totalSlots) && mouseX <= k + ARROW_DOWN_BUTTON_X + 8 && mouseX >= k + ARROW_DOWN_BUTTON_X && mouseY <= l + ARROW_DOWN_BUTTON_Y + 8 && mouseY >= l + ARROW_DOWN_BUTTON_Y)
            drawTexturedModalRect(k + ARROW_DOWN_BUTTON_X, l + ARROW_DOWN_BUTTON_Y, 16, 248, 8, 8)
        else
            drawTexturedModalRect(k + ARROW_DOWN_BUTTON_X, l + ARROW_DOWN_BUTTON_Y, 24, 248, 8, 8)

        GuiInventory.drawEntityOnScreen(k + 25 + 196, l + 83, 25, (k + 25 + 196).toFloat() - this.oldMouseX, (l + 83 - 50).toFloat() - this.oldMouseY, this.mc.player)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        val k = this.guiLeft
        val l = this.guiTop
        if ((mouseButton == 0 || mouseButton == 1) && mouseX <= k + BAG_BUTTON_X + 8 && mouseX >= k + BAG_BUTTON_X && mouseY <= l + BAG_BUTTON_Y + 8 && mouseY >= l + BAG_BUTTON_Y) {
            PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(0))

            val soundHandler = mc.soundHandler
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f))
        }


        if ((slotPos != 0) && (mouseButton == 0 || mouseButton == 1) && mouseX <= k + ARROW_UP_BUTTON_X + 8 && mouseX >= k + ARROW_UP_BUTTON_X && mouseY <= l + ARROW_UP_BUTTON_Y + 8 && mouseY >= l + ARROW_UP_BUTTON_Y) {
            PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(1, slotPos - 1))

            val soundHandler = mc.soundHandler
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f))
        }
        if ((slotPos != totalSlots) && (mouseButton == 0 || mouseButton == 1) && mouseX <= k + ARROW_DOWN_BUTTON_X + 8 && mouseX >= k + ARROW_DOWN_BUTTON_X && mouseY <= l + ARROW_DOWN_BUTTON_Y + 8 && mouseY >= l + ARROW_DOWN_BUTTON_Y) {
            PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(1, slotPos + 1))

            val soundHandler = mc.soundHandler
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f))
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        val k = this.guiLeft
        val l = this.guiTop


        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 0f, 100f)
        this.mc.textureManager.bindTexture(background)
        drawTexturedModalRect(192, 78, 192, 78, 60, 16)
        val text = I18n.format("${LibMisc.MOD_ID}.misc.page", slotPos + 1)
        mc.fontRenderer.drawString(text, (223 - mc.fontRenderer.getStringWidth(text) / 2), 82, 0x404040)
        GlStateManager.popMatrix()

        if (mouseX <= k + BAG_BUTTON_X + 8 && mouseX >= k + BAG_BUTTON_X && mouseY <= l + BAG_BUTTON_Y + 8 && mouseY >= l + BAG_BUTTON_Y)
            drawHoveringText(I18n.format("${LibMisc.MOD_ID}.misc.back_to_hat"), mouseX - k, mouseY - l)

        if ((slotPos != 0) && mouseX <= k + ARROW_UP_BUTTON_X + 8 && mouseX >= k + ARROW_UP_BUTTON_X && mouseY <= l + ARROW_UP_BUTTON_Y + 8 && mouseY >= l + ARROW_UP_BUTTON_Y)
            drawHoveringText(I18n.format("${LibMisc.MOD_ID}.misc.go_to", slotPos), mouseX - k, mouseY - l)
        if ((slotPos != totalSlots) && mouseX <= k + ARROW_DOWN_BUTTON_X + 8 && mouseX >= k + ARROW_DOWN_BUTTON_X && mouseY <= l + ARROW_DOWN_BUTTON_Y + 8 && mouseY >= l + ARROW_DOWN_BUTTON_Y)
            drawHoveringText(I18n.format("${LibMisc.MOD_ID}.misc.go_to", slotPos + 2), mouseX - k, mouseY - l)


    }

    companion object {
        val background = ResourceLocation("classyhats", "textures/gui/hat_bag.png")
    }
}
