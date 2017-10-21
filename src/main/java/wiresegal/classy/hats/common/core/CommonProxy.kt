package wiresegal.classy.hats.common.core

import com.teamwizardry.librarianlib.core.common.RecipeGeneratorHandler
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.nonnullListOf
import net.minecraft.init.Blocks
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

        RecipeGeneratorHandler.addShapedRecipe("phantom", ItemStack(ItemPhantomThread),
                "LW ",
                "WSW",
                " WL",
                'L', "gemLapis",
                'W', "string",
                'S', "stickWood")


        RecipeGeneratorHandler.addShapedRecipe("oak_stand", "wood_hat_stand", ItemStack(BlockHatStand, 1, 0), // Oak
                "LLL",
                " F ",
                "FLF",
                'L', ItemStack(Blocks.PLANKS, 1, 0), // Oak Planks
                'F', ItemStack(Blocks.WOODEN_SLAB, 1, 0)) // Oak Slab

        RecipeGeneratorHandler.addShapedRecipe("spruce_stand", "wood_hat_stand", ItemStack(BlockHatStand, 1, 1), // Spruce
                "LLL",
                " F ",
                "FLF",
                'L', ItemStack(Blocks.PLANKS, 1, 1), // Spruce Planks
                'F', ItemStack(Blocks.WOODEN_SLAB, 1, 1)) // Spruce Slab

        RecipeGeneratorHandler.addShapedRecipe("birch_stand", "wood_hat_stand", ItemStack(BlockHatStand, 1, 2), // Birch
                "LLL",
                " F ",
                "FLF",
                'L', ItemStack(Blocks.PLANKS, 1, 2), // Birch Planks
                'F', ItemStack(Blocks.WOODEN_SLAB, 1, 2)) // Birch Slab

        RecipeGeneratorHandler.addShapedRecipe("jungle_stand", "wood_hat_stand", ItemStack(BlockHatStand, 1, 3), // Jungle
                "LLL",
                " F ",
                "FLF",
                'L', ItemStack(Blocks.PLANKS, 1, 3), // Spruce Planks
                'F', ItemStack(Blocks.WOODEN_SLAB, 1, 3)) // Spruce Slab

        RecipeGeneratorHandler.addShapedRecipe("acacia_stand", "wood_hat_stand", ItemStack(BlockHatStand, 1, 4), // Acacia
                "LLL",
                " F ",
                "FLF",
                'L', ItemStack(Blocks.PLANKS, 1, 4), // Acacia Planks
                'F', ItemStack(Blocks.WOODEN_SLAB, 1, 4)) // Acacia Slab

        RecipeGeneratorHandler.addShapedRecipe("dark_oak_stand", "wood_hat_stand", ItemStack(BlockHatStand, 1, 5), // Dark Oak
                "LLL",
                " F ",
                "FLF",
                'L', ItemStack(Blocks.PLANKS, 1, 5), // Dark Oak Planks
                'F', ItemStack(Blocks.WOODEN_SLAB, 1, 5)) // Dark Oak Slab

        RecipeGeneratorHandler.addShapedRecipe("generic_stand", "wood_hat_stand", ItemStack(BlockHatStand, 1, 0), // Catchall for wood
                "LLL",
                " F ",
                "FLF",
                'L', "plankWood",
                'F', "slabWood")

        RecipeGeneratorHandler.addShapedRecipe("stone_stand", ItemStack(BlockHatStand, 1, 6), // Stone
                "LLL",
                " F ",
                "FLF",
                'L', "stone",
                'F', ItemStack(Blocks.STONE_SLAB, 1, 0)) // Stone Slab

        RecipeGeneratorHandler.addShapedRecipe("quartz_stand", ItemStack(BlockHatStand, 1, 7), // Quartz
                "LLL",
                " F ",
                "FLF",
                'L', "blockQuartz",
                'F', ItemStack(Blocks.STONE_SLAB, 1, 7)) // Quartz Slab

        RecipeGeneratorHandler.addShapedRecipe("obsidian_stand", ItemStack(BlockHatStand, 1, 8), // Obsidian
                "LLL",
                " F ",
                "FLF",
                'L', "obsidian",
                'F', "ingotIron")
    }

    open fun post(e: FMLPostInitializationEvent) {
        // NO-OP
    }
}
