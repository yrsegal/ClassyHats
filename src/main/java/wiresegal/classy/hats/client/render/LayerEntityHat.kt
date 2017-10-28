package wiresegal.classy.hats.client.render

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityZombieVillager
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.item.ItemStack
import wiresegal.classy.hats.common.core.AttachmentHandler
import wiresegal.classy.hats.common.hat.ItemHat

/**
 * @author WireSegal
 * Created at 1:06 PM on 9/1/17.
 */
class LayerEntityHat(val modelRenderer: ModelRenderer?) : LayerRenderer<EntityLivingBase> {

    private var stack = ItemStack(ItemHat)

    override fun doRenderLayer(player: EntityLivingBase, limbSwing: Float, limbSwingAmount: Float, partialTicks: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float, scale: Float) {
        val data = player.entityData
        if (data.hasKey(AttachmentHandler.customKey)) {
            val str = data.getString(AttachmentHandler.customKey)
            if (str.isNotEmpty()) {
                ItemNBTHelper.setString(stack, "hat", str)
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
