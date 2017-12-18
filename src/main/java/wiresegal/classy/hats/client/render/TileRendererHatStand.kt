package wiresegal.classy.hats.client.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import wiresegal.classy.hats.block.BlockHatContainer.TileHatContainer

object TileRendererHatStand : TileEntitySpecialRenderer<TileHatContainer>() {
    override fun render(te: TileHatContainer, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val stack = te.inventory.handler.getStackInSlot(0)
        if (!stack.isEmpty) {
            GlStateManager.pushMatrix()

            val amount = rendererDispatcher.world.getCombinedLight(te.pos.up(), 0)
            val lX = (amount % (1 shl 16)).toFloat()
            val lY = (amount / (1 shl 16)).toFloat()
            val prevX = OpenGlHelper.lastBrightnessX
            val prevY = OpenGlHelper.lastBrightnessY

            GlStateManager.translate(x + 0.5, y + 1.4625, z + 0.5)
            GlStateManager.scale(0.9375, 0.9375, 0.9375)
            GlStateManager.rotate(te.angle, 0.0f, -1.0f, 0.0f)

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lX, lY)
            Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.NONE)
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY)

            GlStateManager.popMatrix()
        }
    }
}
