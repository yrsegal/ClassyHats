package wiresegal.classy.hats.client.core

import com.google.common.base.Charsets
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.resources.FolderResourcePack
import net.minecraft.client.resources.IResourcePack
import net.minecraft.entity.EntityList
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.ReflectionHelper
import org.apache.logging.log4j.Logger
import wiresegal.classy.hats.LibMisc
import wiresegal.classy.hats.client.render.LayerEntityHat
import wiresegal.classy.hats.client.render.LayerHat
import wiresegal.classy.hats.client.render.TESRStand
import wiresegal.classy.hats.common.core.CommonProxy
import wiresegal.classy.hats.common.core.HatConfigHandler
import wiresegal.classy.hats.common.misc.BlockHatStand
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream


/**
 * @author WireSegal
 * Created at 8:32 PM on 8/31/17.
 */
class ClientProxy : CommonProxy() {

    val DEFAULT_RESOURCE_PACKS = arrayOf("aD", "field_110449_ao", "defaultResourcePacks")

    companion object {
        private lateinit var log: Logger
    }

    override fun pre(e: FMLPreInitializationEvent) {
        super.pre(e)
        Minecraft.getMinecraft().resourceManager
        val packs = ReflectionHelper.getPrivateValue<MutableList<IResourcePack>, Minecraft>(Minecraft::class.java, Minecraft.getMinecraft(), *DEFAULT_RESOURCE_PACKS)
        packs.add(CustomFolderResourcePack(HatConfigHandler.rpl, "classyhats_extra"))

        log = e.modLog

        KeyHandler

        MinecraftForge.EVENT_BUS.register(object {
            @SubscribeEvent
            fun onStitch(e: TextureStitchEvent.Pre) {
                e.map.registerSprite(ResourceLocation(LibMisc.MOD_ID, "gui/hat_slot"))
            }
        })
    }

    override fun init(e: FMLInitializationEvent) {
        super.init(e)

        ClientRegistry.bindTileEntitySpecialRenderer(BlockHatStand.TileHatStand::class.java, TESRStand)

        val skinMap = Minecraft.getMinecraft().renderManager.skinMap
        var render = skinMap["default"]
        render?.addLayer(LayerHat(render.mainModel.bipedHead))

        render = skinMap["slim"]
        render?.addLayer(LayerHat(render.mainModel.bipedHead))

        val map = Minecraft.getMinecraft().renderManager.entityRenderMap
        for (entity in EntityList.getEntityNameList()) if (entity.toString() in HatConfigHandler.names) {
            val clazz = EntityList.getClass(entity)
            val entityRenderer = map[clazz]
            if (entityRenderer is RenderLiving) {
                val main = entityRenderer.getMainModel()
                val renderer = if (main is ModelBiped) main.bipedHead else null
                entityRenderer.addLayer(LayerEntityHat(renderer))
            }
        }
    }

    class CustomFolderResourcePack(source: File, private val name: String) : FolderResourcePack(source) {

        override fun hasResourceName(name: String): Boolean {
            return super.hasResourceName(name)
        }

        override fun getPackName(): String {
            return "FMLFileResourcePack:" + name
        }

        @Throws(IOException::class)
        override fun getInputStreamByName(resourceName: String): InputStream {
            try {
                return super.getInputStreamByName(resourceName)
            } catch (ioe: IOException) {
                if ("pack.mcmeta" == resourceName) {
                    ClientProxy.log.debug("Mod %s is missing a pack.mcmeta file, substituting a dummy one", name)
                    return ByteArrayInputStream(("{\n" +
                            " \"pack\": {\n" +
                            "   \"description\": \"dummy FML pack for " + name + "\",\n" +
                            "   \"pack_format\": 2\n" +
                            "}\n" +
                            "}").toByteArray(Charsets.UTF_8))
                } else
                    throw ioe
            }

        }
    }

}
