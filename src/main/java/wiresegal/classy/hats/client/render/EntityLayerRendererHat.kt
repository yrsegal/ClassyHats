package wiresegal.classy.hats.client.render

import com.teamwizardry.librarianlib.features.helpers.setNBTString
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityZombieVillager
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.item.ItemStack
import wiresegal.classy.hats.ClassyHatsContent
import wiresegal.classy.hats.event.CapabilityEvents

class EntityLayerRendererHat(private val modelRenderer: ModelRenderer?) : LayerRenderer<EntityLivingBase> {
    override fun doRenderLayer(player: EntityLivingBase, limbSwing: Float, limbSwingAmount: Float, partialTicks: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float, scale: Float) {
        val stack = ItemStack(ClassyHatsContent.HAT)
        val data = player.entityData
        if (data.hasKey(CapabilityEvents.customKey)) {
            val str = data.getString(CapabilityEvents.customKey)
            if (str.isNotEmpty()) {
                stack.setNBTString("hat", str)
                GlStateManager.pushMatrix()

                if (player.isSneaking) GlStateManager.translate(0.0f, 0.2f, 0.0f)

                val flag = player is EntityVillager || player is EntityZombieVillager

                if (player.isChild && player !is EntityVillager) {
                    GlStateManager.translate(0.0f, 0.5f * scale, 0.0f)
                    GlStateManager.scale(0.7f, 0.7f, 0.7f)
                    GlStateManager.translate(0.0f, 16.0f * scale, 0.0f)
                }

                if (flag) GlStateManager.translate(0.0f, 0.1875f, 0.0f)

                this.modelRenderer?.postRender(0.0625f)
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

                GlStateManager.translate(0.0f, -0.25f, 0.0f)
                GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
                GlStateManager.scale(0.625f, -0.625f, -0.625f)

                Minecraft.getMinecraft().itemRenderer.renderItem(player, stack, ItemCameraTransforms.TransformType.HEAD)

                GlStateManager.popMatrix()
            }
        }
    }

    override fun shouldCombineTextures() = false
}
