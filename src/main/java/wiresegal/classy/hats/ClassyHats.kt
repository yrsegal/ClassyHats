package wiresegal.classy.hats

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import wiresegal.classy.hats.common.core.CommonProxy

/**
 * @author WireSegal
 * Created at 8:28 PM on 8/31/17.
 */
@Mod(modid = LibMisc.MOD_ID, name = LibMisc.NAME, version = LibMisc.VERSION, dependencies = LibMisc.DEPENDENCIES)
class ClassyHats {

    @Mod.EventHandler
    fun pre(e: FMLPreInitializationEvent) {
        INSTANCE = this

        PROXY.pre(e)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        PROXY.init(e)
    }

    @Mod.EventHandler
    fun post(e: FMLPostInitializationEvent) {
        PROXY.post(e)
    }

    @Mod.EventHandler
    fun serverStart(e: FMLServerStartingEvent) {
        // NO-OP
    }

    companion object {

        val LOGGER: Logger = LogManager.getLogger("ClassyHats")

        @SidedProxy(clientSide = LibMisc.CLIENT_PROXY, serverSide = LibMisc.COMMON_PROXY)
        lateinit var PROXY: CommonProxy

        lateinit var INSTANCE: ClassyHats
            private set
    }
}
