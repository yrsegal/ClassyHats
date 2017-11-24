package wiresegal.classy.hats.common.gui

import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraftforge.common.crafting.IRecipeContainer
import wiresegal.classy.hats.LibMisc
import wiresegal.classy.hats.common.core.AttachmentHandler
import wiresegal.classy.hats.common.hat.ItemHat


/**
 * @author WireSegal
 * Created at 4:11 PM on 9/1/17.
 */

class ContainerHat(playerInv: InventoryPlayer, private val thePlayer: EntityPlayer) : Container(), IRecipeContainer {
    /**
     * The crafting matrix inventory.
     */
    private val craftMatrix = InventoryCrafting(this, 2, 2)
    private val craftResult = InventoryCraftResult()
    val hat = AttachmentHandler.getCapability(thePlayer)

    init {
        this.addSlotToContainer(SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 154, 28))

        for (i in 0..1) for (j in 0..1)
            this.addSlotToContainer(Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18))

        for (k in 0..3) {
            val slot = equipmentSlots[k]
            this.addSlotToContainer(object : Slot(playerInv, 36 + (3 - k), 8, 8 + k * 18) {
                override fun getSlotStackLimit() = 1

                override fun isItemValid(stack: ItemStack) = stack.item.isValidArmor(stack, slot, thePlayer)

                override fun canTakeStack(playerIn: EntityPlayer): Boolean {
                    val itemstack = this.stack
                    return if (!itemstack.isEmpty && !playerIn.isCreative && EnchantmentHelper.hasBindingCurse(itemstack)) false else super.canTakeStack(playerIn)
                }

                override fun getSlotTexture() = ItemArmor.EMPTY_SLOT_NAMES[slot.index]
            })
        }

        this.addSlotToContainer(object : Slot(HatInventoryWrapper(hat), 0, 77, 8) {
            override fun isItemValid(stack: ItemStack) = stack.item == ItemHat
            override fun getSlotTexture() = "${LibMisc.MOD_ID}:gui/hat_slot"
        })

        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18))

        for (i in 0..8)
            this.addSlotToContainer(Slot(playerInv, i, 8 + i * 18, 142))

        this.addSlotToContainer(object : Slot(playerInv, 40, 77, 62) {
            override fun getSlotTexture() = "minecraft:items/empty_armor_slot_shield"
        })

        this.onCraftMatrixChanged(this.craftMatrix)
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    override fun onCraftMatrixChanged(par1IInventory: IInventory) {
        slotChangedCraftingGrid(thePlayer.world, thePlayer, craftMatrix, craftResult)
    }

    /**
     * Called when the container is closed.
     */
    override fun onContainerClosed(player: EntityPlayer) {
        super.onContainerClosed(player)
        (0..3)
                .map { this.craftMatrix.removeStackFromSlot(it) }
                .filterNot { it.isEmpty }
                .forEach { player.dropItem(it, false) }

        this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY)
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var stack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot.hasStack) {
            val inSlot = slot.stack
            stack = inSlot.copy()

            val targetSlot = EntityLiving.getSlotForItemStack(stack)

            val slotShift = 1

            if (index == 0) {
                if (!this.mergeItemStack(inSlot, 9 + slotShift, 45 + slotShift, true))
                    return ItemStack.EMPTY
                slot.onSlotChange(inSlot, stack)
            } else if (index < 9) {
                if (!this.mergeItemStack(inSlot, 9 + slotShift, 45 + slotShift, false))
                    return ItemStack.EMPTY
            } else if (index >= 9 && index < 9 + slotShift) {
                if (!this.mergeItemStack(inSlot, 9 + slotShift, 45 + slotShift, false))
                    return ItemStack.EMPTY
            } else if (targetSlot.slotType == EntityEquipmentSlot.Type.ARMOR && !(this.inventorySlots[8 - targetSlot.index] as Slot).hasStack) {
                val i = 8 - targetSlot.index
                if (!this.mergeItemStack(inSlot, i, i + 1, false))
                    return ItemStack.EMPTY
            } else if (targetSlot == EntityEquipmentSlot.OFFHAND && !(this.inventorySlots[45 + slotShift] as Slot).hasStack) {
                if (!this.mergeItemStack(inSlot, 45 + slotShift, 46 + slotShift, false))
                    return ItemStack.EMPTY
            } else if (stack.item == ItemHat) {
                if (!this.inventorySlots[9].hasStack && !this.mergeItemStack(inSlot, 9, 10, false))
                    return ItemStack.EMPTY
            } else if (index >= 9 + slotShift && index < 36 + slotShift) {
                if (!this.mergeItemStack(inSlot, 36 + slotShift, 45 + slotShift, false))
                    return ItemStack.EMPTY
            } else if (index >= 36 + slotShift && index < 45 + slotShift) {
                if (!this.mergeItemStack(inSlot, 9 + slotShift, 36 + slotShift, false))
                    return ItemStack.EMPTY
            } else if (!this.mergeItemStack(inSlot, 9 + slotShift, 45 + slotShift, false))
                return ItemStack.EMPTY

            if (inSlot.isEmpty) slot.putStack(ItemStack.EMPTY)
            else slot.onSlotChanged()

            if (inSlot.count == stack.count)
                return ItemStack.EMPTY

            val taken = slot.onTake(playerIn, inSlot)

            if (index == 0) playerIn.dropItem(taken, false)
        }

        return stack
    }

    override fun canMergeSlot(stack: ItemStack, slot: Slot): Boolean {
        return slot.inventory !== this.craftResult && super.canMergeSlot(stack, slot)
    }

    override fun getCraftResult() = craftResult
    override fun getCraftMatrix() = craftMatrix

    companion object {
        private val equipmentSlots = arrayOf(EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET)
    }
}
