package wiresegal.classyhats.client.gui

import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiButtonImage
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.gui.recipebook.GuiRecipeBook
import net.minecraft.client.gui.recipebook.IRecipeShownListener
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.InventoryEffectRenderer
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.Slot
import net.minecraft.util.ResourceLocation
import wiresegal.classyhats.ClassyHats
import wiresegal.classyhats.container.ContainerHatInventory
import wiresegal.classyhats.network.PacketHatGuiOpen
import java.io.IOException


/**
 * @author WireSegal
 * Created at 4:09 PM on 9/1/17.
 */
class GuiHatInventory(player: EntityPlayer) : InventoryEffectRenderer(ContainerHatInventory(player.inventory, player)), IRecipeShownListener {

    private var lastMouseX: Float = 0.0F
    private var lastMouseY: Float = 0.0F

    private lateinit var recipeButton: GuiButtonImage

    private val recipeBookGui = GuiRecipeBook()
    private var widthTooNarrow: Boolean = false
    private var buttonClicked: Boolean = false

    init {
        this.allowUserInput = true
    }

    override fun initGui() {
        this.buttonList.clear()

        super.initGui()

        this.widthTooNarrow = this.width < 379
        this.recipeBookGui.func_194303_a(this.width, this.height, this.mc, this.widthTooNarrow, (this.inventorySlots as ContainerHatInventory).craftMatrix)
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize)
        this.recipeButton = GuiButtonImage(10, this.guiLeft + 104, this.height / 2 - 22, 20, 18, 178, 0, 19, GuiContainer.INVENTORY_BACKGROUND)
        this.buttonList.add(this.recipeButton)
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        this.fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 0x404040)
        val k = this.guiLeft
        val l = this.guiTop
        if (mouseX <= k + 81 + 8 && mouseX >= k + 81 && mouseY <= l + 39 + 8 && mouseY >= l + 39)
            drawHoveringText(I18n.format("button.${ClassyHats.ID}.hat_inventory.open_bag"), mouseX - k, mouseY - l)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (!this.recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton)) {
            if (!this.widthTooNarrow || !this.recipeBookGui.isVisible) {
                super.mouseClicked(mouseX, mouseY, mouseButton)
                val k = this.guiLeft
                val l = this.guiTop
                if ((mouseButton == 0 || mouseButton == 1) && mouseX <= k + 81 + 8 && mouseX >= k + 81 && mouseY <= l + 39 + 8 && mouseY >= l + 39) {
                    PacketHandler.NETWORK.sendToServer(PacketHatGuiOpen(1))

                    val soundHandler = mc.soundHandler
                    soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                }
            }
        }
    }

    override fun hasClickedOutside(mouseX: Int, mouseY: Int, cornerX: Int, cornerY: Int): Boolean {
        val flag = mouseX < cornerX || mouseY < cornerY || mouseX >= cornerX + this.xSize || mouseY >= cornerY + this.ySize
        return this.recipeBookGui.hasClickedOutside(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize) && flag
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.lastMouseX = mouseX.toFloat()
        this.lastMouseY = mouseY.toFloat()
        this.drawDefaultBackground()

        if (this.recipeBookGui.isVisible && this.widthTooNarrow) {
            this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
            this.recipeBookGui.render(mouseX, mouseY, partialTicks)
        } else {
            this.recipeBookGui.render(mouseX, mouseY, partialTicks)
            super.drawScreen(mouseX, mouseY, partialTicks)
            this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, false, partialTicks)
        }

        this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun isPointInRegion(rectX: Int, rectY: Int, rectWidth: Int, rectHeight: Int, pointX: Int, pointY: Int): Boolean {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        if (this.buttonClicked) {
            this.buttonClicked = false
        } else {
            super.mouseReleased(mouseX, mouseY, state)
        }
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 10) {
            this.recipeBookGui.initVisuals(this.widthTooNarrow, (this.inventorySlots as ContainerHatInventory).craftMatrix)
            this.recipeBookGui.toggleVisibility()
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize)
            this.recipeButton.setPosition(this.guiLeft + 104, this.height / 2 - 22)
            this.buttonClicked = true
        }
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

        GuiInventory.drawEntityOnScreen(k + 51, l + 75, 30, (k + 51).toFloat() - this.lastMouseX, (l + 75 - 50).toFloat() - this.lastMouseY, this.mc.player)
    }

    @Throws(IOException::class)
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (!this.recipeBookGui.keyPressed(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode)
        }
    }

    override fun handleMouseClick(slotIn: Slot?, slotId: Int, mouseButton: Int, type: ClickType) {
        if(slotIn != null){
            super.handleMouseClick(slotIn, slotId, mouseButton, type)
            this.recipeBookGui.slotClicked(slotIn)
        } else
            this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, mouseButton, type, this.mc.player)

    }

    override fun recipesUpdated() {
        this.recipeBookGui.recipesUpdated()
    }

    override fun onGuiClosed() {
        this.recipeBookGui.removed()
        super.onGuiClosed()
    }

    override fun func_194310_f(): GuiRecipeBook {
        return this.recipeBookGui
    }

    companion object {
        val background = ResourceLocation(ClassyHats.ID, "textures/gui/hat_inventory.png")
    }
}
