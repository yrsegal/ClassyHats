package wiresegal.classyhats.proxy

import com.teamwizardry.librarianlib.core.common.RegistrationHandler
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import wiresegal.classyhats.ClassyHats
import wiresegal.classyhats.ClassyHatsConfig
import wiresegal.classyhats.ClassyHatsContent
import wiresegal.classyhats.ClassyHatsRegistry
import wiresegal.classyhats.capability.CapabilityHatContainer
import wiresegal.classyhats.capability.data.ClassyHatStorage
import wiresegal.classyhats.capability.data.IHatContainer
import wiresegal.classyhats.event.CapabilityEvents
import wiresegal.classyhats.recipe.PhantomThreadCamoRecipe
import wiresegal.classyhats.recipe.PhantomThreadRecipe
import wiresegal.classyhats.util.LootTableFactory
import wiresegal.classyhats.util.SidedGuiHandler

open class CommonProxy {
    open fun onPreInit(event: FMLPreInitializationEvent) {
        CapabilityManager.INSTANCE.register(IHatContainer::class.java, ClassyHatStorage(), ::CapabilityHatContainer)
        ClassyHatsContent
        CapabilityEvents
        LootTableFactory
        ClassyHatsConfig.loadPreInit(event)
        RegistrationHandler.register(PhantomThreadRecipe)
        RegistrationHandler.register(PhantomThreadCamoRecipe)
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
        const val CLIENT = "wiresegal.classyhats.proxy.ClientProxy"
        const val SERVER = "wiresegal.classyhats.proxy.CommonProxy"
    }
}
