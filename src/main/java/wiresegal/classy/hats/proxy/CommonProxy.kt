package wiresegal.classy.hats.proxy

import com.teamwizardry.librarianlib.core.common.RegistrationHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import wiresegal.classy.hats.ClassyHats
import wiresegal.classy.hats.ClassyHatsConfig
import wiresegal.classy.hats.ClassyHatsContent
import wiresegal.classy.hats.ClassyHatsRegistry
import wiresegal.classy.hats.capability.CapabilityHatContainer
import wiresegal.classy.hats.capability.data.ClassyHatStorage
import wiresegal.classy.hats.capability.data.IHatContainer
import wiresegal.classy.hats.event.CapabilityEvents
import wiresegal.classy.hats.event.PhantomThreadEvents
import wiresegal.classy.hats.recipe.PhantomThreadCamoRecipe
import wiresegal.classy.hats.recipe.PhantomThreadRecipe
import wiresegal.classy.hats.util.LootTableFactory
import wiresegal.classy.hats.util.SidedGuiHandler

open class CommonProxy {
    open fun onPreInit(event: FMLPreInitializationEvent) {
        ClassyHatsRegistry.collectExternalHats(event)
        CapabilityManager.INSTANCE.register(IHatContainer::class.java, ClassyHatStorage(), ::CapabilityHatContainer)
        ClassyHatsContent
        CapabilityEvents
        LootTableFactory
        ClassyHatsConfig.loadPreInit(event)
        RegistrationHandler.register(PhantomThreadRecipe)
        RegistrationHandler.register(PhantomThreadCamoRecipe)
        MinecraftForge.EVENT_BUS.register(CapabilityEvents)
        MinecraftForge.EVENT_BUS.register(PhantomThreadEvents)
    }

    open fun onInit(event: FMLInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(ClassyHats.INSTANCE, SidedGuiHandler)
    }

    open fun onPostInit(event: FMLPostInitializationEvent) {
        val size = ClassyHatsConfig.hats.size - 1
        val defaultSize = ClassyHatsRegistry.HATS.size
        ClassyHats.LOGGER.info("Loaded $size hats.")
        if (size < defaultSize)
            ClassyHats.LOGGER.info("Your lack of hats disturbs me.")
        else if (size - defaultSize > 20)
            ClassyHats.LOGGER.info("Wow, that's a lot of hats!")
    }

    companion object {
        const val CLIENT = "wiresegal.classy.hats.proxy.ClientProxy"
        const val SERVER = "wiresegal.classy.hats.proxy.CommonProxy"
    }
}
