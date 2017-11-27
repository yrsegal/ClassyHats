package wiresegal.classy.hats.proxy

import com.google.common.base.Charsets
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.resources.FolderResourcePack
import net.minecraft.client.resources.IResourcePack
import net.minecraft.entity.EntityList
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.ReflectionHelper
import wiresegal.classy.hats.ClassyHats
import wiresegal.classy.hats.ClassyHatsConfig
import wiresegal.classy.hats.block.BlockHatContainer.TileHatContainer
import wiresegal.classy.hats.client.keybind.KeyBindHandler
import wiresegal.classy.hats.client.render.EntityLayerRendererHat
import wiresegal.classy.hats.client.render.PlayerLayerRendererHat
import wiresegal.classy.hats.client.render.TileRendererHatStand
import wiresegal.classy.hats.event.GuiTextureEvents
import wiresegal.classy.hats.event.HatStandRenderEvents
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

class ClientProxy : CommonProxy() {
    private val defaultResourcePackFields = arrayOf("aD", "field_110449_ao", "defaultResourcePacks")

    override fun onPreInit(event: FMLPreInitializationEvent) {
        super.onPreInit(event)
        MinecraftForge.EVENT_BUS.register(KeyBindHandler)
        ClientRegistry.registerKeyBinding(KeyBindHandler.KEY)
        ReflectionHelper.getPrivateValue<MutableList<IResourcePack>, Minecraft>(
                Minecraft::class.java, Minecraft.getMinecraft(), *defaultResourcePackFields
        ).add(CustomFolderResourcePack(ClassyHatsConfig.rpl, "classyhats_extra"))
        MinecraftForge.EVENT_BUS.register(HatStandRenderEvents)
        MinecraftForge.EVENT_BUS.register(GuiTextureEvents)
    }

    override fun onInit(event: FMLInitializationEvent) {
        super.onInit(event)

        ClientRegistry.bindTileEntitySpecialRenderer(TileHatContainer::class.java, TileRendererHatStand)

        val skinMap = Minecraft.getMinecraft().renderManager.skinMap
        var render = skinMap["default"]
        render?.addLayer(PlayerLayerRendererHat(render.mainModel.bipedHead))

        render = skinMap["slim"]
        render?.addLayer(PlayerLayerRendererHat(render.mainModel.bipedHead))

        val map = Minecraft.getMinecraft().renderManager.entityRenderMap
        for (entity in EntityList.getEntityNameList()) if (entity.toString() in ClassyHatsConfig.names) {
            val clazz = EntityList.getClass(entity)
            val entityRenderer = map[clazz]
            if (entityRenderer is RenderLiving) {
                val main = entityRenderer.getMainModel()
                val renderer = if (main is ModelBiped) main.bipedHead else null
                entityRenderer.addLayer(EntityLayerRendererHat(renderer))
            }
        }
    }

    class CustomFolderResourcePack(source: File, private val name: String) : FolderResourcePack(source) {
        override fun getPackName(): String {
            return "FMLFileResourcePack:" + name
        }

        @Throws(IOException::class)
        override fun getInputStreamByName(resourceName: String): InputStream = try {
            super.getInputStreamByName(resourceName)
        } catch (exception: IOException) {
            if ("pack.mcmeta" == resourceName) {
                ClassyHats.LOGGER.debug("Mod %s is missing a pack.mcmeta file, substituting a dummy one", name)
                ByteArrayInputStream((
                        "{\n" +
                        " \"pack\": {\n" +
                        "   \"description\": \"dummy FML pack for " + name + "\",\n" +
                        "   \"pack_format\": 2\n" +
                        "}\n" +
                        "}").toByteArray(Charsets.UTF_8))
            } else
                throw exception
        }
    }
}
