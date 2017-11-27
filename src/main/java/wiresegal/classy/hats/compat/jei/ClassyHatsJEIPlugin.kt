package wiresegal.classy.hats.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import wiresegal.classy.hats.client.gui.GuiHatInventory

@JEIPlugin
class ClassyHatsJEIPlugin : IModPlugin {
    override fun register(registry: IModRegistry) {
        registry.recipeTransferRegistry.addRecipeTransferHandler(HatInvRecipeTransferInfo())
        registry.addRecipeClickArea(GuiHatInventory::class.java, 135, 29, 16, 13, VanillaRecipeCategoryUid.CRAFTING)
    }
}
