package wiresegal.classy.hats.client.core

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.FolderResourcePack
import net.minecraft.client.resources.IResourcePack
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.ReflectionHelper
import wiresegal.classy.hats.client.render.LayerHat
import wiresegal.classy.hats.common.core.CommonProxy
import wiresegal.classy.hats.common.core.HatConfigHandler


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
        packs.add(FolderResourcePack(HatConfigHandler.rpl))

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

}
