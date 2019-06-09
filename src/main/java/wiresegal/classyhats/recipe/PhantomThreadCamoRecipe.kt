package wiresegal.classyhats.recipe

import com.teamwizardry.librarianlib.features.helpers.getNBTBoolean
import com.teamwizardry.librarianlib.features.helpers.getNBTCompound
import com.teamwizardry.librarianlib.features.helpers.setNBTCompound
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
import wiresegal.classyhats.item.ItemPhantomThread

object PhantomThreadCamoRecipe : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {
    init { registryName = ResourceLocation(ClassyHats.ID, "phantom_camo") }

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        val ret = NonNullList.withSize(inv.sizeInventory, ItemStack.EMPTY)
        for (i in ret.indices) {
            val stack = inv.getStackInSlot(i)
            val phantom = stack.hasTagCompound() && stack.getNBTBoolean(PhantomThreadEvents.PHANTOM_TAG, false)
            val armor = EntityEquipmentSlot.values().any { it.slotType == EntityEquipmentSlot.Type.ARMOR && stack.item.isValidArmor(stack, it, null) }
            if (phantom) {
                val container = ItemStack(stack.getNBTCompound(PhantomThreadEvents.PHANTOM_ITEM_TAG) ?: NBTTagCompound())
                ret[i] = container
            } else if (!armor)
                ret[i] = ForgeHooks.getContainerItem(inv.getStackInSlot(i))
        }
        return ret
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width * height >= 2
    }

    override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
        val threadedTypes = mutableSetOf<EntityEquipmentSlot>()
        var armor: ItemStack = ItemStack.EMPTY
        var threaded: ItemStack = ItemStack.EMPTY
        var thread: ItemStack = ItemStack.EMPTY

        mainLoop@ for (i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)

            for (equipType in EntityEquipmentSlot.values()) if (equipType.slotType == EntityEquipmentSlot.Type.ARMOR)
                if (stack.item.isValidArmor(stack, equipType, null)) {
                    val phantom = stack.hasTagCompound() && stack.getNBTBoolean(PhantomThreadEvents.PHANTOM_TAG, false)
                    if (phantom) {
                        if (threaded.isNotEmpty)
                            return ItemStack.EMPTY
                        threaded = stack
                        threadedTypes.addAll(EntityEquipmentSlot.values().filter { it.slotType == EntityEquipmentSlot.Type.ARMOR && stack.item.isValidArmor(stack, it, null) })
                    } else {
                        if (armor.isNotEmpty)
                            return ItemStack.EMPTY
                        armor = stack
                    }

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

        if (threadedTypes.none { armor.item.isValidArmor(armor, it, null) })
            return ItemStack.EMPTY

        val armorCopy = threaded.copy()
        armorCopy.setNBTCompound(PhantomThreadEvents.PHANTOM_ITEM_TAG, armor.writeToNBT(NBTTagCompound()))
        return armorCopy
    }

    override fun getRecipeOutput(): ItemStack = ItemStack.EMPTY

    override fun matches(inv: InventoryCrafting, worldIn: World?): Boolean {
        val threadedTypes = mutableSetOf<EntityEquipmentSlot>()
        var armor: ItemStack = ItemStack.EMPTY
        var threaded: ItemStack = ItemStack.EMPTY
        var thread: ItemStack = ItemStack.EMPTY

        mainLoop@ for (i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)

            for (equipType in EntityEquipmentSlot.values()) if (equipType.slotType == EntityEquipmentSlot.Type.ARMOR)
                if (stack.item.isValidArmor(stack, equipType, null)) {
                    val phantom = stack.hasTagCompound() && stack.getNBTBoolean(PhantomThreadEvents.PHANTOM_TAG, false)
                    if (phantom) {
                        if (threaded.isNotEmpty)
                            return false
                        threaded = stack
                        threadedTypes.addAll(EntityEquipmentSlot.values().filter { it.slotType == EntityEquipmentSlot.Type.ARMOR && stack.item.isValidArmor(stack, it, null) })
                    } else {
                        if (armor.isNotEmpty)
                            return false
                        armor = stack
                    }
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

        if (threadedTypes.none { armor.item.isValidArmor(armor, it, null) })
            return false

        return armor.isNotEmpty && threaded.isNotEmpty
    }

    override fun isDynamic() = true
}