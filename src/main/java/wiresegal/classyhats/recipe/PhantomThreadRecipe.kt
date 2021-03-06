package wiresegal.classyhats.recipe

import com.teamwizardry.librarianlib.features.helpers.getNBTBoolean
import com.teamwizardry.librarianlib.features.helpers.getNBTCompound
import com.teamwizardry.librarianlib.features.helpers.removeNBTEntry
import com.teamwizardry.librarianlib.features.helpers.setNBTBoolean
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.registries.IForgeRegistryEntry
import wiresegal.classyhats.ClassyHats
import wiresegal.classyhats.event.PhantomThreadEvents
import wiresegal.classyhats.item.ItemHat
import wiresegal.classyhats.item.ItemPhantomThread

object PhantomThreadRecipe : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {
    init { registryName = ResourceLocation(ClassyHats.ID, "phantom_invis") }

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        val ret = NonNullList.withSize(inv.sizeInventory, ItemStack.EMPTY)
        for (i in ret.indices) {
            val stack = inv.getStackInSlot(i)
            if (stack.hasTagCompound()&& stack.getNBTBoolean(PhantomThreadEvents.PHANTOM_TAG, false)) {
                val container = ItemStack(stack.getNBTCompound(PhantomThreadEvents.PHANTOM_ITEM_TAG) ?: NBTTagCompound())
                ret[i] = container
            } else
                ret[i] = ForgeHooks.getContainerItem(inv.getStackInSlot(i))
        }
        return ret
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width * height >= 2
    }

    override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
        var armor: ItemStack = ItemStack.EMPTY
        var thread: ItemStack = ItemStack.EMPTY

        mainLoop@ for (i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)

            if (stack.item !is ItemHat) for (equipType in EntityEquipmentSlot.values()) if (equipType.slotType == EntityEquipmentSlot.Type.ARMOR)
                if (stack.item.isValidArmor(stack, equipType, null)) {
                    if (armor.isNotEmpty)
                        return ItemStack.EMPTY
                    armor = stack
                    continue@mainLoop
                }

            if (stack.item is ItemPhantomThread) {
                if (thread.isNotEmpty)
                    return ItemStack.EMPTY
                thread = stack
                continue@mainLoop
            }

            if (stack.isNotEmpty)
                return ItemStack.EMPTY
        }

        val armorCopy = armor.copy()
        val phantom = armorCopy.getNBTBoolean(PhantomThreadEvents.PHANTOM_TAG, false)
        val camo = ItemStack(armorCopy.getNBTCompound(PhantomThreadEvents.PHANTOM_ITEM_TAG) ?: NBTTagCompound()).isNotEmpty

        if (!camo || thread.isNotEmpty) {
            if (phantom)
                armorCopy.removeNBTEntry(PhantomThreadEvents.PHANTOM_TAG)
            else
                armorCopy.setNBTBoolean(PhantomThreadEvents.PHANTOM_TAG, true)
        }

        if (camo)
            armorCopy.removeNBTEntry(PhantomThreadEvents.PHANTOM_ITEM_TAG)

        val tag = armorCopy.tagCompound
        if (tag != null && tag.size == 0)
            armorCopy.tagCompound = null
        return armorCopy
    }

    override fun getRecipeOutput(): ItemStack = ItemStack.EMPTY

    override fun matches(inv: InventoryCrafting, worldIn: World?): Boolean {
        var armor: ItemStack = ItemStack.EMPTY
        var thread: ItemStack = ItemStack.EMPTY

        mainLoop@ for (i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)

            if (stack.item !is ItemHat) for (equipType in EntityEquipmentSlot.values()) if (equipType.slotType == EntityEquipmentSlot.Type.ARMOR)
                if (stack.item.isValidArmor(stack, equipType, null)) {
                    if (armor.isNotEmpty)
                        return false
                    armor = stack
                    continue@mainLoop
                }

            if (stack.item is ItemPhantomThread) {
                if (thread.isNotEmpty)
                    return false
                thread = stack
                continue@mainLoop
            }

            if (stack.isNotEmpty)
                return false
        }

        val camo = ItemStack(armor.getNBTCompound(PhantomThreadEvents.PHANTOM_ITEM_TAG) ?: NBTTagCompound()).isNotEmpty

        return (camo || thread.isNotEmpty) && armor.isNotEmpty
    }

    override fun isDynamic() = true
}