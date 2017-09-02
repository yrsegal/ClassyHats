package wiresegal.classy.hats.common.core

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.nonnullListOf
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.ShapedOreRecipe
import wiresegal.classy.hats.ClassyHats
import wiresegal.classy.hats.common.gui.HatsGuiFactory
import wiresegal.classy.hats.common.hat.BaseHatStorage
import wiresegal.classy.hats.common.hat.CapabilityHatsStorage
import wiresegal.classy.hats.common.hat.IHatStorage
import wiresegal.classy.hats.common.hat.ItemHat
import wiresegal.classy.hats.common.misc.ItemPhantomThread

/**
 * @author WireSegal
 * Created at 8:32 PM on 8/31/17.
 */
open class CommonProxy {
    open fun pre(e: FMLPreInitializationEvent) {

        CapabilityManager.INSTANCE.register(IHatStorage::class.java, CapabilityHatsStorage(), ::BaseHatStorage)
        AttachmentHandler

        object : ModCreativeTab() {
            override val iconStack: ItemStack
                get() {
                    val items = nonnullListOf<ItemStack>()
                    ItemHat.getSubItems(ItemHat, null, items)
                    if (items.isEmpty())
                        return ItemStack(ItemHat)
                    return items[0]
                }
        }

        ItemPhantomThread

        HatConfigHandler.loadPreInit(e)
        ItemHat
    }

    open fun init(e: FMLInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(ClassyHats.INSTANCE, HatsGuiFactory)

        GameRegistry.addRecipe(ShapedOreRecipe(ItemPhantomThread,
                "LW ",
                "WSW",
                " WL",
                'L', "gemLapis",
                'W', ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE),
                'S', "stickWood"))
    }

    open fun post(e: FMLPostInitializationEvent) {
        // NO-OP
    }
}
