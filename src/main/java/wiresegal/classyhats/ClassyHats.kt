package wiresegal.classyhats

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.nonnullListOf
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import wiresegal.classyhats.command.CommandChangeHat
import wiresegal.classyhats.proxy.CommonProxy

@Mod(modid = ClassyHats.ID, name = ClassyHats.NAME, version = ClassyHats.VERSION)
class ClassyHats {
    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        PROXY.onPreInit(event)
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        PROXY.onInit(event)
    }

    @Mod.EventHandler
    fun onPostInit(event: FMLPostInitializationEvent) {
        PROXY.onPostInit(event)
    }

    @Mod.EventHandler
    fun onServerStart(event: FMLServerStartingEvent) {
        event.registerServerCommand(CommandChangeHat)
    }

    companion object {
        const val ID = "classyhats"
        const val NAME = "Classy Hats"
        const val VERSION = "%VERSION%"

        val TAB = object : ModCreativeTab() {

            override fun getTranslationKey() = "tab.$tabLabel.name"

            override fun hasSearchBar() = true

            override fun getBackgroundImageName() = "item_search.png"

            override val iconStack: ItemStack
                get() {
                    val items = nonnullListOf<ItemStack>()
                    ClassyHatsContent.HAT.getSubItems(this, items)
                    return when {
                        items.isEmpty() -> ItemStack(ClassyHatsContent.HAT)
                        else -> items[0]
                    }
                }
        }

        val LOGGER: Logger = LogManager.getLogger(NAME)

        @SidedProxy(clientSide = CommonProxy.CLIENT, serverSide = CommonProxy.SERVER)
        lateinit var PROXY: CommonProxy

        @Mod.Instance(ID)
        lateinit var INSTANCE: ClassyHats
    }
}
