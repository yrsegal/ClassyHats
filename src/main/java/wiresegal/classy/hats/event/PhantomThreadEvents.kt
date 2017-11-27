package wiresegal.classy.hats.event

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.inventory.EntityEquipmentSlot.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.client.event.RenderTooltipEvent
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import wiresegal.classy.hats.ClassyHats

object PhantomThreadEvents {
    const val PHANTOM_TAG = "classy_hat_invisible"
    const val PHANTOM_ITEM_TAG = "classy_hat_disguise"

    private var lastHeadStack = ItemStack.EMPTY
    private var lastChestStack = ItemStack.EMPTY
    private var lastLegsStack = ItemStack.EMPTY
    private var lastFeetStack = ItemStack.EMPTY

    private var captureSounds = false

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    fun onRenderTooltip(event: RenderTooltipEvent) {
        if (event.stack.hasTagCompound() && ItemNBTHelper.getBoolean(event.stack, PHANTOM_TAG, false)) {
            val locale = "tooltip.${ClassyHats.ID}.phantom"
            val nbt = ItemNBTHelper.getCompound(event.stack, PHANTOM_ITEM_TAG)
            val container = ItemStack(nbt ?: NBTTagCompound())
            if (!container.isEmpty) {
                var formatting = container.rarity.rarityColor.toString()
                if (container.hasDisplayName()) formatting += TextFormatting.ITALIC.toString()
                formatting += container.displayName
                TooltipHelper.addToTooltip(event.lines, formatting + (locale + ".camo"))
            } else TooltipHelper.addToTooltip(event.lines, locale + ".desc")
            TooltipHelper.addToTooltip(event.lines, locale)
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    fun onRenderLivingPre(event: RenderLivingEvent.Pre<EntityLivingBase>) {
        captureSounds = true
        lastHeadStack = setPhantomStack(event.entity, HEAD)
        lastChestStack = setPhantomStack(event.entity, CHEST)
        lastLegsStack = setPhantomStack(event.entity, LEGS)
        lastFeetStack = setPhantomStack(event.entity, FEET)
        captureSounds = false
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    fun onRenderLivingPost(event: RenderLivingEvent.Post<EntityLivingBase>) {
        captureSounds = true
        setSlotFor(event.entity, HEAD, lastHeadStack)
        setSlotFor(event.entity, CHEST, lastChestStack)
        setSlotFor(event.entity, LEGS, lastLegsStack)
        setSlotFor(event.entity, FEET, lastFeetStack)
        captureSounds = false
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    fun sound(event: PlaySoundAtEntityEvent) {
        if (captureSounds) event.isCanceled = true
    }

    private fun setSlotFor(entity: EntityLivingBase, slot: EntityEquipmentSlot, stack: ItemStack) {
        if (stack.isNotEmpty) entity.setItemStackToSlot(slot, stack)
    }

    private fun setPhantomStack(entity: EntityLivingBase, slot: EntityEquipmentSlot) : ItemStack {
        val stack = entity.getItemStackFromSlot(slot)
        return if (stack.hasTagCompound() && ItemNBTHelper.getBoolean(stack, PHANTOM_TAG, false)) {
            entity.setItemStackToSlot(slot, ItemStack(getPhantomTag(stack) ?: NBTTagCompound()))
            stack
        } else ItemStack.EMPTY
    }

    private fun getPhantomTag(stack: ItemStack): NBTTagCompound? {
        return if (ItemNBTHelper.verifyExistence(stack, PHANTOM_TAG)) {
            ItemNBTHelper.getNBT(stack, false).getCompoundTag(PHANTOM_TAG)
        } else null
    }
}