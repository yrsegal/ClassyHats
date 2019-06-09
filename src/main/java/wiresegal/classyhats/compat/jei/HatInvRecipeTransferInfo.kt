package wiresegal.classyhats.compat.jei

import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo
import net.minecraft.inventory.Slot
import wiresegal.classyhats.container.ContainerHatInventory

class HatInvRecipeTransferInfo : IRecipeTransferInfo<ContainerHatInventory> {
    override fun getContainerClass(): Class<ContainerHatInventory> = ContainerHatInventory::class.java

    override fun getRecipeCategoryUid(): String = VanillaRecipeCategoryUid.CRAFTING

    override fun canHandle(container: ContainerHatInventory): Boolean = true

    override fun getRecipeSlots(container: ContainerHatInventory): MutableList<Slot> = container.inventorySlots.subList(1, 5)

    override fun getInventorySlots(container: ContainerHatInventory): MutableList<Slot> = container.inventorySlots.subList(10, 46)
}
