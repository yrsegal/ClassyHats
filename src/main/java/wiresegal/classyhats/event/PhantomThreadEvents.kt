package wiresegal.classyhats.event

import com.teamwizardry.librarianlib.features.helpers.getNBTBoolean
import com.teamwizardry.librarianlib.features.helpers.getNBTCompound
import com.teamwizardry.librarianlib.features.helpers.hasNBTEntry
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.EntityLivingBase
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.inventory.EntityEquipmentSlot.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import wiresegal.classyhats.ClassyHats


@Mod.EventBusSubscriber(modid = ClassyHats.ID)
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
    @JvmStatic
    fun onRenderTooltip(event: ItemTooltipEvent) {
        if (event.itemStack.hasTagCompound() && event.itemStack.getNBTBoolean(PHANTOM_TAG, false)) {
            val locale = "tooltip.${ClassyHats.ID}.phantom"
            val nbt = event.itemStack.getNBTCompound(PHANTOM_ITEM_TAG) ?: NBTTagCompound()
            val container = ItemStack(nbt)
            if (!container.isEmpty) {
                event.toolTip.add(I18n.format("$locale.camo") + " " + container.rarity.color + if (container.hasDisplayName()) TextFormatting.ITALIC else "" + container.displayName)
            } else TooltipHelper.addToTooltip(event.toolTip, "$locale.desc")
            TooltipHelper.addToTooltip(event.toolTip, locale)
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
    fun sound(event: PlaySoundAtEntityEvent) {
        if (captureSounds) event.isCanceled = true
    }

    private fun setSlotFor(entity: EntityLivingBase, slot: EntityEquipmentSlot, stack: ItemStack) {
        if (stack.isNotEmpty) entity.setItemStackToSlot(slot, stack)
    }

    private fun setPhantomStack(entity: EntityLivingBase, slot: EntityEquipmentSlot) : ItemStack {
        val stack = entity.getItemStackFromSlot(slot)
        return if (stack.hasTagCompound() && stack.getNBTBoolean(PHANTOM_TAG, false)) {
            val comp = getPhantomTag(stack) ?: NBTTagCompound()
            entity.setItemStackToSlot(slot, ItemStack(comp))
            stack
        } else ItemStack.EMPTY
    }

    private fun getPhantomTag(stack: ItemStack): NBTTagCompound? {
        return if (stack.hasNBTEntry(PHANTOM_ITEM_TAG)) {
            stack.tagCompound?.getCompoundTag(PHANTOM_ITEM_TAG)
        } else null
    }
}
