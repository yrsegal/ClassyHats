package wiresegal.classy.hats.common.misc

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.RecipeSorter
import wiresegal.classy.hats.LibMisc

/**
 * @author WireSegal
 * Created at 6:10 PM on 9/1/17.
 */

val PHANTOM_TAG = "classy_hat_invisible"

object ItemPhantomThread : ItemMod("phantom_thread") {

    init {
        setMaxStackSize(1)

        MinecraftForge.EVENT_BUS.register(this)

        RecipeSorter.register("${LibMisc.MOD_ID}:phantom", PhantomRecipe::class.java, RecipeSorter.Category.SHAPELESS, "")
        GameRegistry.addRecipe(PhantomRecipe)
    }

    override fun getContainerItem(itemStack: ItemStack): ItemStack
            = itemStack.copy().apply { count = 1 }

    override fun hasEffect(stack: ItemStack) = true

    override fun getRarity(stack: ItemStack) = EnumRarity.UNCOMMON

    override fun addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: MutableList<String>, advanced: Boolean) {
        val desc = stack.unlocalizedName + ".desc"
        val used = if (LibrarianLib.PROXY.canTranslate(desc)) desc else "${desc}0"
        if (LibrarianLib.PROXY.canTranslate(used)) {
            TooltipHelper.addToTooltip(tooltip, used)
            var i = 0
            while (LibrarianLib.PROXY.canTranslate("$desc${++i}"))
                TooltipHelper.addToTooltip(tooltip, "$desc$i")
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun tooltip(e: ItemTooltipEvent) {
        val stack = e.itemStack
        if (stack.hasTagCompound()) {
            val phantom = ItemNBTHelper.getBoolean(stack, PHANTOM_TAG, false)

            if (phantom)
                e.toolTip.add(1, TooltipHelper.local("classyhats.misc.phantom").replace("&".toRegex(), "§"))
        }
    }

    private var head: ItemStack = ItemStack.EMPTY
    private var chest: ItemStack = ItemStack.EMPTY
    private var legs: ItemStack = ItemStack.EMPTY
    private var feet: ItemStack = ItemStack.EMPTY

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun preRender(e: RenderPlayerEvent.Pre) {
        val head = e.entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD)
        val chest = e.entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST)
        val legs = e.entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.LEGS)
        val feet = e.entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.FEET)

        if (ItemNBTHelper.getBoolean(head, PHANTOM_TAG, false)) {
            this.head = head
            e.entityPlayer.inventory.armorInventory[EntityEquipmentSlot.HEAD.index] = ItemStack.EMPTY
        } else this.head = ItemStack.EMPTY

        if (ItemNBTHelper.getBoolean(chest, PHANTOM_TAG, false)) {
            this.chest = chest
            e.entityPlayer.inventory.armorInventory[EntityEquipmentSlot.CHEST.index] = ItemStack.EMPTY
        } else this.chest = ItemStack.EMPTY

        if (ItemNBTHelper.getBoolean(legs, PHANTOM_TAG, false)) {
            this.legs = legs
            e.entityPlayer.inventory.armorInventory[EntityEquipmentSlot.LEGS.index] = ItemStack.EMPTY
        } else this.legs = ItemStack.EMPTY

        if (ItemNBTHelper.getBoolean(feet, PHANTOM_TAG, false)) {
            this.feet = feet
            e.entityPlayer.inventory.armorInventory[EntityEquipmentSlot.FEET.index] = ItemStack.EMPTY
        } else this.feet = ItemStack.EMPTY
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun postRender(e: RenderPlayerEvent.Post) {
        if (head.isNotEmpty) e.entityPlayer.inventory.armorInventory[EntityEquipmentSlot.HEAD.index] = head
        if (chest.isNotEmpty) e.entityPlayer.inventory.armorInventory[EntityEquipmentSlot.CHEST.index] = chest
        if (legs.isNotEmpty) e.entityPlayer.inventory.armorInventory[EntityEquipmentSlot.LEGS.index] = legs
        if (feet.isNotEmpty) e.entityPlayer.inventory.armorInventory[EntityEquipmentSlot.FEET.index] = feet
    }
}

object PhantomRecipe : IRecipe {
    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv)
    }

    override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
        var armor: ItemStack = ItemStack.EMPTY
        var thread: ItemStack = ItemStack.EMPTY

        mainLoop@ for (i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)

            for (equipType in EntityEquipmentSlot.values()) if (equipType.slotType == EntityEquipmentSlot.Type.ARMOR)
                if (stack.item.isValidArmor(stack, equipType, null)) {
                    if (armor.isNotEmpty)
                        return ItemStack.EMPTY
                    armor = stack
                    continue@mainLoop
                }

            if (stack.item == ItemPhantomThread) {
                if (thread.isNotEmpty)
                    return ItemStack.EMPTY
                thread = stack
                continue@mainLoop
            }

            if (stack.isNotEmpty)
                return ItemStack.EMPTY
        }

        val armorCopy = armor.copy()
        if (ItemNBTHelper.getBoolean(armorCopy, PHANTOM_TAG, false))
            ItemNBTHelper.removeEntry(armorCopy, PHANTOM_TAG)
        else
            ItemNBTHelper.setBoolean(armorCopy, PHANTOM_TAG, true)
        val tag = armorCopy.tagCompound
        if (tag != null && tag.size == 0)
            armorCopy.tagCompound = null
        return armorCopy
    }

    override fun getRecipeOutput(): ItemStack = ItemStack.EMPTY

    override fun getRecipeSize() = 10

    override fun matches(inv: InventoryCrafting, worldIn: World?): Boolean {
        var armor: ItemStack = ItemStack.EMPTY
        var thread: ItemStack = ItemStack.EMPTY

        mainLoop@ for (i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)

            for (equipType in EntityEquipmentSlot.values()) if (equipType.slotType == EntityEquipmentSlot.Type.ARMOR)
                if (stack.item.isValidArmor(stack, equipType, null)) {
                    if (armor.isNotEmpty)
                        return false
                    armor = stack
                    continue@mainLoop
                }

            if (stack.item == ItemPhantomThread) {
                if (thread.isNotEmpty)
                    return false
                thread = stack
                continue@mainLoop
            }

            if (stack.isNotEmpty)
                return false
        }

        return thread.isNotEmpty && armor.isNotEmpty
    }
}
