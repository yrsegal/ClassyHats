package wiresegal.classyhats.client.gui

import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.util.ResourceLocation
import wiresegal.classyhats.ClassyHats
import wiresegal.classyhats.capability.CapabilityHatContainer
import wiresegal.classyhats.container.ContainerHatBag
import wiresegal.classyhats.network.PacketHatGuiOpen
import wiresegal.classyhats.network.PacketSyncLastSelectedSection


/**
 * @author WireSegal
 * Created at 4:09 PM on 9/1/17.
 */
class GuiHatBag(player: EntityPlayer) : GuiContainer(ContainerHatBag(player.inventory, player, CapabilityHatContainer.getCapability(player).currentHatSection)) {

    private var lastMouseX: Float = 0.0F
    private var lastMouseY: Float = 0.0F

    private val bagButtonX = 192
    private val bagButtonY = 143

    private val upButtonX = 194
    private val upButtonY = 3

    private val downButtonX = 194
    private val downButtonY = 95

    private val totalSlots = CapabilityHatContainer.HAT_SIZE / 50 - 1

    private var slotPos = CapabilityHatContainer.getCapability(player).currentHatSection

    init {
        allowUserInput = true
        xSize = 252
        ySize = 193
        slotPos = Math.max(0, Math.min(totalSlots, slotPos))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        lastMouseX = mouseX.toFloat()
        lastMouseY = mouseY.toFloat()
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(partTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        mc.textureManager.bindTexture(TEXTURE)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
        if (mouseX <= guiLeft + bagButtonX + 8 && mouseX >= guiLeft + bagButtonX && mouseY <= guiTop + bagButtonY + 8 && mouseY >= guiTop + bagButtonY)
            drawTexturedModalRect(guiLeft + bagButtonX, guiTop + bagButtonY, 0, 240, 8, 8)
        else drawTexturedModalRect(guiLeft + bagButtonX, guiTop + bagButtonY, 0, 248, 8, 8)
        if ((slotPos != 0) && mouseX <= guiLeft + upButtonX + 8 && mouseX >= guiLeft + upButtonX && mouseY <= guiTop + upButtonY + 8 && mouseY >= guiTop + upButtonY)
            drawTexturedModalRect(guiLeft + upButtonX, guiTop + upButtonY, 16, 240, 8, 8)
        else drawTexturedModalRect(guiLeft + upButtonX, guiTop + upButtonY, 24, 240, 8, 8)
        if ((slotPos != totalSlots) && mouseX <= guiLeft + downButtonX + 8 && mouseX >= guiLeft + downButtonX && mouseY <= guiTop + downButtonY + 8 && mouseY >= guiTop + downButtonY)
            drawTexturedModalRect(guiLeft + downButtonX, guiTop + downButtonY, 16, 248, 8, 8)
        else drawTexturedModalRect(guiLeft + downButtonX, guiTop + downButtonY, 24, 248, 8, 8)
        GuiInventory.drawEntityOnScreen(guiLeft + 25 + 196, guiTop + 83, 25, (guiLeft + 25 + 196).toFloat() - lastMouseX, (guiTop + 83 - 50).toFloat() - lastMouseY, mc.player)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        if ((mouseButton == 0 || mouseButton == 1) && mouseX <= guiLeft + bagButtonX + 8 && mouseX >= guiLeft + bagButtonX && mouseY <= guiTop + bagButtonY + 8 && mouseY >= guiTop + bagButtonY) {
            PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(0))
            val soundHandler = mc.soundHandler
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f))
        }
        if ((slotPos != 0) && (mouseButton == 0 || mouseButton == 1) && mouseX <= guiLeft + upButtonX + 8 && mouseX >= guiLeft + upButtonX && mouseY <= guiTop + upButtonY + 8 && mouseY >= guiTop + upButtonY) {
            (inventorySlots as ContainerHatBag).setSlot(slotPos - 1)
            slotPos = Math.max(0, Math.min(totalSlots, slotPos - 1))
            val soundHandler = mc.soundHandler
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f))
        }
        if ((slotPos != totalSlots) && (mouseButton == 0 || mouseButton == 1) && mouseX <= guiLeft + downButtonX + 8 && mouseX >= guiLeft + downButtonX && mouseY <= guiTop + downButtonY + 8 && mouseY >= guiTop + downButtonY) {
            (inventorySlots as ContainerHatBag).setSlot(slotPos + 1)
            slotPos = Math.max(0, Math.min(totalSlots, slotPos + 1))
            val soundHandler = mc.soundHandler
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f))
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 0f, 100f)
        this.mc.textureManager.bindTexture(TEXTURE)
        drawTexturedModalRect(192, 78, 192, 78, 60, 16)
        val text = I18n.format("button.${ClassyHats.ID}.hat_bag.page", slotPos + 1, totalSlots + 1)
        mc.fontRenderer.drawString(text, (223 - mc.fontRenderer.getStringWidth(text) / 2), 82, 0x404040)
        GlStateManager.popMatrix()
        if (mouseX <= guiLeft + bagButtonX + 8 && mouseX >= guiLeft + bagButtonX && mouseY <= guiTop + bagButtonY + 8 && mouseY >= guiTop + bagButtonY)
            drawHoveringText(I18n.format("button.${ClassyHats.ID}.hat_bag.close"), mouseX - guiLeft, mouseY - guiTop)
        if ((slotPos != 0) && mouseX <= guiLeft + upButtonX + 8 && mouseX >= guiLeft + upButtonX && mouseY <= guiTop + upButtonY + 8 && mouseY >= guiTop + upButtonY)
            drawHoveringText(I18n.format("button.${ClassyHats.ID}.hat_bag.prev_page", slotPos), mouseX - guiLeft, mouseY - guiTop)
        if ((slotPos != totalSlots) && mouseX <= guiLeft + downButtonX + 8 && mouseX >= guiLeft + downButtonX && mouseY <= guiTop + downButtonY + 8 && mouseY >= guiTop + downButtonY)
            drawHoveringText(I18n.format("button.${ClassyHats.ID}.hat_bag.next_page", slotPos + 2), mouseX - guiLeft, mouseY - guiTop)
    }

    override fun onGuiClosed() {
        val pos = (inventorySlots as ContainerHatBag).slotPos
        PacketHandler.NETWORK.sendToServer(PacketSyncLastSelectedSection(pos))
        CapabilityHatContainer.getCapability(mc.player).currentHatSection = pos
    }

    companion object {
        val TEXTURE = ResourceLocation(ClassyHats.ID, "textures/gui/hat_bag.png")
    }
}
