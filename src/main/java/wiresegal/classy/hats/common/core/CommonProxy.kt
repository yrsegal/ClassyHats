package wiresegal.classy.hats.common.core

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.nonnullListOf
import net.minecraft.item.ItemStack
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import wiresegal.classy.hats.ClassyHats
import wiresegal.classy.hats.common.gui.HatsGuiFactory
import wiresegal.classy.hats.common.hat.BaseHatStorage
import wiresegal.classy.hats.common.hat.CapabilityHatsStorage
import wiresegal.classy.hats.common.hat.IHatStorage
import wiresegal.classy.hats.common.hat.ItemHat
import wiresegal.classy.hats.common.misc.BlockHatStand
import wiresegal.classy.hats.common.misc.ItemPhantomThread
import wiresegal.classy.hats.common.misc.LootTableFactory

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
                    ItemHat.getSubItems(this, items)
                    if (items.isEmpty())
                        return ItemStack(ItemHat)
                    return items[0]
                }
        }

        LootTableFactory

        HatConfigHandler.loadPreInit(e)
        ItemHat


        ItemPhantomThread

        BlockHatStand
    }

    open fun init(e: FMLInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(ClassyHats.INSTANCE, HatsGuiFactory)
    }

    open fun post(e: FMLPostInitializationEvent) {
        // NO-OP
    }
}
