package wiresegal.classy.hats.common.core

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.nonnullListOf
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import wiresegal.classy.hats.common.hat.ItemHat

/**
 * @author WireSegal
 * Created at 8:32 PM on 8/31/17.
 */
open class CommonProxy {
    open fun pre(e: FMLPreInitializationEvent) {
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
        HatConfigHandler.loadPreInit(e)
        ItemHat
    }

    open fun init(e: FMLInitializationEvent) {
        // NO-OP
    }

    open fun post(e: FMLPostInitializationEvent) {
        // NO-OP
    }
}
