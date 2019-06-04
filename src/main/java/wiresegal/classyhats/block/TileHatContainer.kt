package wiresegal.classyhats.block

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.items.ItemStackHandler
import wiresegal.classyhats.item.ItemHat

@TileRegister
open class TileHatContainer : TileMod() {
    @Save
    var angle = 0.0F

    val inventory = ModuleInventory(object : ItemStackHandler() {
        override fun getStackLimit(slot: Int, stack: ItemStack) = if (stack.item is ItemHat) 1 else 0

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean = stack.item is ItemHat

        override fun onContentsChanged(slot: Int) = markDirty()

    })


    open fun isAllowed(stack: ItemStack): Boolean {
        return stack.item is ItemHat
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (compound.hasKey("Items"))
            inventory.handler.deserializeNBT(compound.getCompoundTag("Items"));
    }


    override fun getRenderBoundingBox() = AxisAlignedBB(pos, pos.add(1, 2, 1))


}