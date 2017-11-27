package wiresegal.classy.hats.item

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import wiresegal.classy.hats.ClassyHats

class ItemPhantomThread : ItemMod("phantom_thread") {
    override fun getUnlocalizedName(stack: ItemStack): String {
        return "item.${ClassyHats.ID}.phantom_thread"
    }

    override fun getItemStackLimit(stack: ItemStack?): Int = 1

    override fun hasContainerItem(stack: ItemStack) = true

    override fun getContainerItem(itemStack: ItemStack): ItemStack
            = itemStack.copy().apply { count = 1 }

    override fun hasEffect(stack: ItemStack) = true

    override fun getRarity(stack: ItemStack) = EnumRarity.UNCOMMON

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, flag: ITooltipFlag) {
        val desc = stack.unlocalizedName + ".desc"
        val used = if (LibrarianLib.PROXY.canTranslate(desc)) desc else "${desc}0"
        if (LibrarianLib.PROXY.canTranslate(used)) {
            TooltipHelper.addToTooltip(tooltip, used)
            var i = 0
            while (LibrarianLib.PROXY.canTranslate("$desc${++i}")) {
                TooltipHelper.addToTooltip(tooltip, "$desc$i")
            }
        }
    }
}
