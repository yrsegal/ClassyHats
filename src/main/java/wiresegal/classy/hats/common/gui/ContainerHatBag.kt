package wiresegal.classy.hats.common.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler
import wiresegal.classy.hats.LibMisc
import wiresegal.classy.hats.common.core.AttachmentHandler
import wiresegal.classy.hats.common.hat.BaseHatStorage.Companion.HAT_SIZE
import wiresegal.classy.hats.common.hat.ItemHat


/**
 * @author WireSegal
 * Created at 4:11 PM on 9/1/17.
 */

class ContainerHatBag(playerInv: InventoryPlayer, thePlayer: EntityPlayer, var slotPos: Int) : Container() {

    val hat = AttachmentHandler.getCapability(thePlayer)

    val idxPlayerHatStart = 0
    val idxPlayerHatEnd: Int
    val idxHatsStart: Int
    val idxHatsEnd: Int
    val idxPlayerInvStart: Int
    val idxPlayerInvEnd: Int
    val idxHotbarStart: Int
    val idxHotbarEnd: Int

    init {
        this.addSlotToContainer(object : Slot(HatInventoryWrapper(hat), 0, 188, 110) {
            override fun isItemValid(stack: ItemStack) = stack.item == ItemHat
            override fun getSlotTexture() = "${LibMisc.MOD_ID}:gui/hat_slot"
        })
        idxPlayerHatEnd = inventorySlots.size

        idxHatsStart = inventorySlots.size

        val totalSlots = HAT_SIZE / 50 - 1
        slotPos = Math.max(0, Math.min(totalSlots, slotPos))

        for (j in 0..4)
            for (i in 0..9)
                this.addSlotToContainer(object : SlotItemHandler(hat.hats, slotPos * 50 + i + j * 10, 8 + i * 18, 8 + j * 18) {
                    override fun isItemValid(stack: ItemStack) = stack.item == ItemHat
                })
        idxHatsEnd = inventorySlots.size

        idxPlayerInvStart = inventorySlots.size
        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(playerInv, j + (i + 1) * 9, 17 + j * 18, 110 + i * 18))
        idxPlayerInvEnd = inventorySlots.size

        idxHotbarStart = inventorySlots.size
        for (i in 0..8)
            this.addSlotToContainer(Slot(playerInv, i, 17 + i * 18, 168))
        idxHotbarEnd = inventorySlots.size

        this.addSlotToContainer(object : Slot(playerInv, 40, 188, 168) {
            override fun getSlotTexture() = "minecraft:items/empty_armor_slot_shield"
        })
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return true
    }

    fun setSlot(slotPos: Int) {
        val totalSlots = HAT_SIZE / 50 - 1
        this.slotPos = Math.max(0, Math.min(totalSlots, slotPos))

        for (j in 0..4)
            for (i in 0..9) {
                val slot = object : SlotItemHandler(hat.hats, this.slotPos * 50 + i + j * 10, 8 + i * 18, 8 + j * 18) {
                    override fun isItemValid(stack: ItemStack) = stack.item == ItemHat
                }
                slot.slotNumber = j * 10 + i + idxHatsStart
                this.inventorySlots[slot.slotNumber] = slot
                this.inventoryItemStacks[slot.slotNumber] = slot.stack
            }
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var stack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot.hasStack) {
            val inSlot = slot.stack
            stack = inSlot.copy()

            if (index in idxPlayerHatStart until idxPlayerHatEnd) {
                if (!this.mergeItemStack(inSlot, idxHatsStart, idxHatsEnd, false))
                    if (!this.mergeItemStack(inSlot, idxPlayerInvStart, idxPlayerInvEnd, false))
                        if (!this.mergeItemStack(inSlot, idxHotbarStart, idxHotbarEnd, false))
                            return ItemStack.EMPTY
            } else if (index in idxHatsStart until idxHatsEnd) {
                if (!this.mergeItemStack(inSlot, idxPlayerHatStart, idxPlayerHatEnd, false))
                    if (!this.mergeItemStack(inSlot, idxPlayerInvStart, idxPlayerInvEnd, false))
                        if (!this.mergeItemStack(inSlot, idxHotbarStart, idxHotbarEnd, false))
                            return ItemStack.EMPTY
            } else if (index in idxPlayerInvStart until idxPlayerInvEnd) {
                if (!this.mergeItemStack(inSlot, idxHatsStart, idxHatsEnd, false))
                    if (!this.mergeItemStack(inSlot, idxPlayerHatStart, idxPlayerHatEnd, false))
                        if (!this.mergeItemStack(inSlot, idxHotbarStart, idxHotbarEnd, false))
                            return ItemStack.EMPTY
            } else if (!this.mergeItemStack(inSlot, idxHatsStart, idxHatsEnd, false))
                if (!this.mergeItemStack(inSlot, idxPlayerHatStart, idxPlayerHatEnd, false))
                    if (!this.mergeItemStack(inSlot, idxPlayerInvStart, idxPlayerInvEnd, false))
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
}
