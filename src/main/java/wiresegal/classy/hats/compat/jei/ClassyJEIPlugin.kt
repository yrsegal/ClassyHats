package wiresegal.classy.hats.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import wiresegal.classy.hats.common.gui.ContainerHat

@JEIPlugin
class ClassyJEIPlugin : IModPlugin {

    override fun register(registry: IModRegistry?) {
        registry?.recipeTransferRegistry?.addRecipeTransferHandler(
                ContainerHat::class.java, VanillaRecipeCategoryUid.CRAFTING, 1, 4, 9, 35
        )
    }

}