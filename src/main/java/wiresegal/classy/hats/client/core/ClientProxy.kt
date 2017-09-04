package wiresegal.classy.hats.client.core

import com.google.common.base.Charsets
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.FolderResourcePack
import net.minecraft.client.resources.IResourcePack
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.ReflectionHelper
import org.apache.logging.log4j.Level
import wiresegal.classy.hats.client.render.LayerHat
import wiresegal.classy.hats.common.core.CommonProxy
import wiresegal.classy.hats.common.core.HatConfigHandler
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

    override fun pre(e: FMLPreInitializationEvent) {
        super.pre(e)
        Minecraft.getMinecraft().resourceManager
        val packs = ReflectionHelper.getPrivateValue<MutableList<IResourcePack>, Minecraft>(Minecraft::class.java, Minecraft.getMinecraft(), *DEFAULT_RESOURCE_PACKS)
        packs.add(CustomFolderResourcePack(HatConfigHandler.rpl, "classyhats_extra"))

        KeyHandler
    }

    override fun init(e: FMLInitializationEvent) {
        super.init(e)

        val skinMap = Minecraft.getMinecraft().renderManager.skinMap
        var render = skinMap["default"]
        render?.addLayer(LayerHat(render.mainModel.bipedHead))

        render = skinMap["slim"]
        render?.addLayer(LayerHat(render.mainModel.bipedHead))
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
                    FMLLog.log(name, Level.DEBUG, "Mod %s is missing a pack.mcmeta file, substituting a dummy one", name)
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
