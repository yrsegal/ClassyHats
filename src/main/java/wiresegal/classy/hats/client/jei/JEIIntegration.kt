package wiresegal.classy.hats.client.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import wiresegal.classy.hats.common.gui.ContainerHat

/**
 * @author WireSegal
 * Created at 8:37 AM on 10/22/17.
 */
@JEIPlugin
class JEIIntegration : IModPlugin {
    override fun register(registry: IModRegistry) {
        registry.recipeTransferRegistry.addRecipeTransferHandler(HatRecipeTransferHandler(registry.jeiHelpers.recipeTransferHandlerHelper()), VanillaRecipeCategoryUid.CRAFTING)
    }
}
