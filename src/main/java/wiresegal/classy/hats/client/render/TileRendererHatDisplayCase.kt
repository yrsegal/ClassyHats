package wiresegal.classy.hats.client.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.math.MathHelper
import wiresegal.classy.hats.ClassyHatsContent
import wiresegal.classy.hats.block.BlockHatContainer.TileHatContainer

object TileRendererHatDisplayCase : TileEntitySpecialRenderer<TileHatContainer>() {
     override fun render(te: TileHatContainer, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val stack = te.inventory.handler.getStackInSlot(0)
        if (!stack.isEmpty && te.blockType == ClassyHatsContent.HAT_DISPLAY_CASE) {
            GlStateManager.pushMatrix()

            val intpol = world.totalWorldTime + partialTicks
            val offset = 0.1F + MathHelper.sin(intpol * 0.1F) * 0.01F

            GlStateManager.translate(x + 0.5, y + offset + 1.4625, z + 0.5)
            GlStateManager.scale(0.9375, 0.9375, 0.9375)
            GlStateManager.rotate(intpol % 360.0F, 0.0f, -1.0f, 0.0f)

            val amount = rendererDispatcher.world.getCombinedLight(te.pos.up(), 0)
            val lX = (amount % (1 shl 16)).toFloat()
            val lY = (amount / (1 shl 16)).toFloat()
            val prevX = OpenGlHelper.lastBrightnessX
            val prevY = OpenGlHelper.lastBrightnessY

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lX, lY)
            Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.NONE)
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY)
            GlStateManager.popMatrix()
        }
    }
}
