package wiresegal.classy.hats.common.hat

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IExtraVariantHolder
import com.teamwizardry.librarianlib.features.base.item.IGlowingItem
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import wiresegal.classy.hats.LibMisc
import wiresegal.classy.hats.client.core.KeyHandler
import wiresegal.classy.hats.client.core.KeyHandler.key
import wiresegal.classy.hats.common.core.AttachmentHandler
import wiresegal.classy.hats.common.core.HatConfigHandler
import wiresegal.classy.hats.common.core.HatConfigHandler.Hat

/**
 * @author WireSegal
 * Created at 8:40 PM on 8/31/17.
 */
object ItemHat : ItemMod("hat"), IExtraVariantHolder, IGlowingItem {

    fun getHat(stack: ItemStack): Hat {
        if (!stack.hasTagCompound()) return HatConfigHandler.missingno
        val hatId = ItemNBTHelper.getString(stack, "hat", null) ?: return HatConfigHandler.missingno
        return HatConfigHandler.hats[hatId] ?: HatConfigHandler.missingno
    }

    fun ofHat(hat: Hat): ItemStack = ofHat(hat.name)

    fun ofHat(name: String, amount: Int = 1): ItemStack {
        val stack = ItemStack(this, amount)
        ItemNBTHelper.setString(stack, "hat", name)
        return stack
    }

    init {
        setMaxStackSize(1)
    }

    override fun getEquipmentSlot(stack: ItemStack) = EntityEquipmentSlot.HEAD

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        val stack = playerIn.getHeldItem(handIn)
        val hatInv = AttachmentHandler.getCapability(playerIn)
        val stackInSlot = hatInv.equipped

        hatInv.equipped = stack.copy().apply { count = 1 }
        playerIn.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f)
        playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, ItemStack.EMPTY)

        if (stackInSlot.isNotEmpty)
            return ActionResult.newResult(EnumActionResult.SUCCESS, stackInSlot.copy())

        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    override fun hasContainerItem(stack: ItemStack) = true

    override fun getContainerItem(itemStack: ItemStack): ItemStack
            = itemStack.copy().apply { count = 1 }

    override val extraVariants: Array<out String>
        get() = HatConfigHandler.hats.values.map { it.name }.toTypedArray()
    override val meshDefinition: ((stack: ItemStack) -> ModelResourceLocation)?
        get() = { ModelHandler.resourceLocations[LibMisc.MOD_ID]!![getHat(it).name] as ModelResourceLocation }

    override fun getSubItems(tab: CreativeTabs?, subItems: NonNullList<ItemStack>) {
        if (isInCreativeTab(tab)) {
            HatConfigHandler.hats.values
                    .filter { it != HatConfigHandler.missingno && !it.elusive }
                    .mapTo(subItems) { ofHat(it) }
            HatConfigHandler.hats.values
                    .filter { it != HatConfigHandler.missingno && it.elusive }
                    .mapTo(subItems) { ofHat(it) }
        }
    }

    override fun getUnlocalizedName(stack: ItemStack)
            = "item.${LibMisc.MOD_ID}:${getHat(stack).name}"

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        val desc = stack.unlocalizedName + ".desc"
        val used = if (LibrarianLib.PROXY.canTranslate(desc)) desc else "${desc}0"
        if (LibrarianLib.PROXY.canTranslate(used)) {
            TooltipHelper.addToTooltip(tooltip, used)
            var i = 0
            while (LibrarianLib.PROXY.canTranslate("$desc${++i}"))
                TooltipHelper.addToTooltip(tooltip, "$desc$i")
        }

        TooltipHelper.addToTooltip(tooltip, "${LibMisc.MOD_ID}.hat_inv_tooltip", KeyHandler.key.displayName)
    }

    override fun getRarity(stack: ItemStack) =
            if (getHat(stack) == HatConfigHandler.missingno) EnumRarity.EPIC
            else if (getHat(stack).elusive) EnumRarity.UNCOMMON
            else EnumRarity.COMMON

    @SideOnly(Side.CLIENT)
    override fun transformToGlow(itemStack: ItemStack, model: IBakedModel): IBakedModel? {
        return IGlowingItem.Helper.wrapperBake(model, false, 99)
    }
}
