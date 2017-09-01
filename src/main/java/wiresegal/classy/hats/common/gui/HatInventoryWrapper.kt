package wiresegal.classy.hats.common.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentTranslation
import wiresegal.classy.hats.common.hat.IHatStorage
import wiresegal.classy.hats.common.hat.ItemHat

/**
 * @author WireSegal
 * Created at 2:10 PM on 9/1/17.
 */
class HatInventoryWrapper(val hats: IHatStorage) : IInventory {
    override fun getField(id: Int)
            = 0

    override fun hasCustomName()
            = false

    override fun markDirty()
            = Unit

    override fun getStackInSlot(index: Int): ItemStack
            = hats.equipped

    override fun decrStackSize(index: Int, count: Int): ItemStack
            = ItemStackHelper.getAndSplit(mutableListOf(hats.equipped), 0, count)

    override fun clear() {
        hats.equipped = ItemStack.EMPTY
    }

    override fun getSizeInventory()
            = 1

    override fun getName()
            = "classyhats.container.hats"

    override fun isEmpty()
            = hats.equipped.isEmpty

    override fun getDisplayName()
            = TextComponentTranslation(name)

    override fun isItemValidForSlot(index: Int, stack: ItemStack)
            = stack.item == ItemHat

    override fun getInventoryStackLimit()
            = 1

    override fun isUsableByPlayer(player: EntityPlayer)
            = !player.isDead

    override fun openInventory(player: EntityPlayer)
            = Unit
    override fun setField(id: Int, value: Int)
            = Unit

    override fun closeInventory(player: EntityPlayer?)
            = Unit

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        hats.equipped = stack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val prev = hats.equipped
        hats.equipped = ItemStack.EMPTY
        return prev
    }

    override fun getFieldCount() = 0
}
