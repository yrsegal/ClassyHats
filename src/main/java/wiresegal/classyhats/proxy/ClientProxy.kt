package wiresegal.classyhats.proxy

import com.google.common.base.Charsets
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.resources.FolderResourcePack
import net.minecraft.client.resources.IResourcePack
import net.minecraft.entity.EntityList
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.ReflectionHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import wiresegal.classyhats.ClassyHats
import wiresegal.classyhats.ClassyHatsConfig
import wiresegal.classyhats.block.BlockHatContainer.TileHatContainer
import wiresegal.classyhats.client.keybind.KeyBindHandler
import wiresegal.classyhats.client.render.EntityLayerRendererHat
import wiresegal.classyhats.client.render.PlayerLayerRendererHat
import wiresegal.classyhats.client.render.TileRendererHatStand
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

@SideOnly(Side.CLIENT)
class ClientProxy : CommonProxy() {
    private val defaultResourcePackFields = arrayOf("aD", "field_110449_ao", "defaultResourcePacks")


    override fun onPreInit(event: FMLPreInitializationEvent) {
        super.onPreInit(event)
        ClientRegistry.registerKeyBinding(KeyBindHandler.KEY)
        ReflectionHelper.getPrivateValue<MutableList<IResourcePack>, Minecraft>(
                Minecraft::class.java, Minecraft.getMinecraft(), *defaultResourcePackFields
        ).add(CustomFolderResourcePack(ClassyHatsConfig.rpl, "classyhats_extra"))
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
