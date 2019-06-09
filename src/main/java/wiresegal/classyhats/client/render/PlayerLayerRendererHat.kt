package wiresegal.classyhats.client.render

import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import wiresegal.classyhats.capability.CapabilityHatContainer

/**
 * @author WireSegal
 * Created at 1:06 PM on 9/1/17.
 */
@SideOnly(Side.CLIENT)
class PlayerLayerRendererHat(private val modelRenderer: ModelRenderer) : LayerRenderer<EntityPlayer> {
    override fun doRenderLayer(player: EntityPlayer, limbSwing: Float, limbSwingAmount: Float, partialTicks: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float, scale: Float) {
        val stack = CapabilityHatContainer.getCapability(player).equipped

        if (!stack.isEmpty) {
            GlStateManager.pushMatrix()

            if (player.isSneaking) GlStateManager.translate(0.0f, 0.2f, 0.0f)

            this.modelRenderer.postRender(0.0625f)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

            GlStateManager.translate(0.0f, -0.25f, 0.0f)
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
            GlStateManager.scale(0.625f, -0.625f, -0.625f)

            Minecraft.getMinecraft().itemRenderer.renderItem(player, stack, ItemCameraTransforms.TransformType.HEAD)

            GlStateManager.popMatrix()
        }
    }

    override fun shouldCombineTextures() = false
}
