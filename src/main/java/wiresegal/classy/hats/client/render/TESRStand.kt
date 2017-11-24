package wiresegal.classy.hats.client.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import wiresegal.classy.hats.common.misc.BlockHatStand


/**
 * @author WireSegal
 * Created at 10:05 PM on 9/4/17.
 */
object TESRStand : TileEntitySpecialRenderer<BlockHatStand.TileHatStand>() {
    override fun render(te: BlockHatStand.TileHatStand, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
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

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun renderSelectionBox(selectionBox: AxisAlignedBB, pos: BlockPos, player: EntityPlayer, partialTicks: Float) {
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

    @SubscribeEvent
    fun onDrawBlockHighlight(event: DrawBlockHighlightEvent) {

        if (event.target.blockPos != null) {
            val pos = event.target.blockPos
            val player = event.player
            val world = player.entityWorld
            val state = world.getBlockState(pos)
            val block = state.block
            if (block is BlockHatStand) {
                for (aabb in block.getCollisionBoxes())
                    renderSelectionBox(aabb, pos, player, event.partialTicks)
                event.isCanceled = true
            }
        }
    }
}
