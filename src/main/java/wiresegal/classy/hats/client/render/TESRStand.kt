package wiresegal.classy.hats.client.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import wiresegal.classy.hats.common.misc.BlockHatStand

/**
 * @author WireSegal
 * Created at 10:05 PM on 9/4/17.
 */
object TESRStand : TileEntitySpecialRenderer<BlockHatStand.TileHatStand>() {
    override fun renderTileEntityAt(te: BlockHatStand.TileHatStand, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        val stack = te.inv.handler.getStackInSlot(0)
        if (!stack.isEmpty) {
            GlStateManager.pushMatrix()

            GlStateManager.translate(x + 0.5, y + 1.4625, z + 0.5)

            GlStateManager.scale(0.9375, 0.9375, 0.9375)

            GlStateManager.rotate(te.angle, 0.0f, -1.0f, 0.0f)

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
