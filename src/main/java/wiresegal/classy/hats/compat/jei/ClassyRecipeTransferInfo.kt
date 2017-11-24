package wiresegal.classy.hats.compat.jei

import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo
import net.minecraft.inventory.Slot
import wiresegal.classy.hats.common.gui.ContainerHat

class ClassyRecipeTransferInfo : IRecipeTransferInfo<ContainerHat> {

    override fun getContainerClass(): Class<ContainerHat> = ContainerHat::class.java

    override fun canHandle(container: ContainerHat): Boolean = true

    override fun getInventorySlots(container: ContainerHat): MutableList<Slot> = container.inventorySlots.subList(9, 46)

    override fun getRecipeSlots(container: ContainerHat): MutableList<Slot> = container.inventorySlots.subList(0, 5)

    override fun getRecipeCategoryUid(): String {
        return VanillaRecipeCategoryUid.CRAFTING
    }

}
