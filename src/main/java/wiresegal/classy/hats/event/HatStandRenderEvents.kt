package wiresegal.classy.hats.event

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import wiresegal.classy.hats.ClassyHatsContent
import wiresegal.classy.hats.block.BlockHatStand

object HatStandRenderEvents {
    @SubscribeEvent
    fun onDrawBlockHighlight(event: DrawBlockHighlightEvent) {
        if (event.target != null && event.target.typeOfHit == RayTraceResult.Type.BLOCK) {
            val pos = event.target.blockPos
            val player = event.player
            val state = player.entityWorld.getBlockState(pos)
            if (state.block is BlockHatStand) {
                for (aabb in ClassyHatsContent.HAT_STAND.getCollisionBoxes(state))
                    renderSelectionBox(aabb, pos, player, event.partialTicks)
                event.isCanceled = true
            }
        }
    }

    private fun renderSelectionBox(selectionBox: AxisAlignedBB, pos: BlockPos, player: EntityPlayer, partialTicks: Float) {
        GlStateManager.disableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        )
        GlStateManager.glLineWidth(2.0f)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        val offsetX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks
        val offsetY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks
        val offsetZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks
        val target = selectionBox.grow(0.002).offset(pos).offset(-offsetX, -offsetY, -offsetZ)
        RenderGlobal.drawSelectionBoundingBox(target, 0.0f, 0.0f, 0.0f, 0.4f)
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
    }
}