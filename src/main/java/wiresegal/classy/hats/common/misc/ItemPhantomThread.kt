package wiresegal.classy.hats.common.misc

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.common.RegistrationHandler
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.EntityLivingBase
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.inventory.EntityEquipmentSlot.Type.ARMOR
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistryEntry
import wiresegal.classy.hats.LibMisc

/**
 * @author WireSegal
 * Created at 6:10 PM on 9/1/17.
 */

val PHANTOM_TAG = "classy_hat_invisible"
val PHANTOM_ITEM_TAG = "classy_hat_disguise"

object ItemPhantomThread : ItemMod("phantom_thread") {

    init {
        setMaxStackSize(1)

        MinecraftForge.EVENT_BUS.register(this)

        RegistrationHandler.register(PhantomRecipe)
        RegistrationHandler.register(PhantomCamoRecipe)
    }

    override fun hasContainerItem(stack: ItemStack) = true

    override fun getContainerItem(itemStack: ItemStack): ItemStack
            = itemStack.copy().apply { count = 1 }

    override fun hasEffect(stack: ItemStack) = true

    override fun getRarity(stack: ItemStack) = EnumRarity.UNCOMMON

    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, flag: ITooltipFlag) {
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

            if (phantom) {
                val container = ItemStack(ItemNBTHelper.getCompound(stack, PHANTOM_ITEM_TAG) ?: NBTTagCompound())
                if (container.isEmpty)
                    e.toolTip.add(1, TooltipHelper.local("classyhats.misc.phantom.desc").replace("&".toRegex(), "§"))
                else
                    e.toolTip.add(1, TooltipHelper.local("classyhats.misc.phantom.camo", container.rarity.rarityColor + (if (container.hasDisplayName()) TextFormatting.ITALIC.toString() else "") + container.displayName).replace("&".toRegex(), "§"))
                e.toolTip.add(1, TooltipHelper.local("classyhats.misc.phantom").replace("&".toRegex(), "§"))
            }
        }
    }

    private var head: ItemStack = ItemStack.EMPTY
    private var chest: ItemStack = ItemStack.EMPTY
    private var legs: ItemStack = ItemStack.EMPTY
    private var feet: ItemStack = ItemStack.EMPTY

    private var captureSounds = false

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun preRender(e: RenderLivingEvent.Pre<EntityLivingBase>) {
        val head = e.entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD)
        val chest = e.entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST)
        val legs = e.entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS)
        val feet = e.entity.getItemStackFromSlot(EntityEquipmentSlot.FEET)

        captureSounds = true

        if (ItemNBTHelper.getBoolean(head, PHANTOM_TAG, false)) {
            this.head = head
            e.entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack(ItemNBTHelper.getCompound(head, PHANTOM_ITEM_TAG) ?: NBTTagCompound()))
        } else this.head = ItemStack.EMPTY

        if (ItemNBTHelper.getBoolean(chest, PHANTOM_TAG, false)) {
            this.chest = chest
            e.entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, ItemStack(ItemNBTHelper.getCompound(chest, PHANTOM_ITEM_TAG) ?: NBTTagCompound())) 
        } else this.chest = ItemStack.EMPTY

        if (ItemNBTHelper.getBoolean(legs, PHANTOM_TAG, false)) {
            this.legs = legs
            e.entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, ItemStack(ItemNBTHelper.getCompound(legs, PHANTOM_ITEM_TAG) ?: NBTTagCompound())) 
        } else this.legs = ItemStack.EMPTY

        if (ItemNBTHelper.getBoolean(feet, PHANTOM_TAG, false)) {
            this.feet = feet
            e.entity.setItemStackToSlot(EntityEquipmentSlot.FEET, ItemStack(ItemNBTHelper.getCompound(feet, PHANTOM_ITEM_TAG) ?: NBTTagCompound())) 
        } else this.feet = ItemStack.EMPTY

        captureSounds = false
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun sound(e: PlaySoundAtEntityEvent) {
        if (captureSounds) e.isCanceled = true
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun postRender(e: RenderLivingEvent.Post<EntityLivingBase>) {
        captureSounds = true
        if (head.isNotEmpty) e.entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, head) 
        if (chest.isNotEmpty) e.entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, chest) 
        if (legs.isNotEmpty) e.entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, legs) 
        if (feet.isNotEmpty) e.entity.setItemStackToSlot(EntityEquipmentSlot.FEET, feet)
        captureSounds = false
    }
}

object PhantomRecipe : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        registryName = ResourceLocation(LibMisc.MOD_ID, "phantom_invis")
    }

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        val ret = NonNullList.withSize(inv.sizeInventory, ItemStack.EMPTY)
        for (i in ret.indices) {
            val stack = inv.getStackInSlot(i)
            if (ItemNBTHelper.getBoolean(stack, PHANTOM_TAG, false)) {
                val container = ItemStack(ItemNBTHelper.getCompound(stack, PHANTOM_ITEM_TAG) ?: NBTTagCompound())
                ret[i] = container
            } else
                ret[i] = ForgeHooks.getContainerItem(inv.getStackInSlot(i))
        }
        return ret
    }

    override fun canFit(width: Int, height: Int): Boolean {
        return width * height >= 2
    }

    override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
        var armor: ItemStack = ItemStack.EMPTY
        var thread: ItemStack = ItemStack.EMPTY

        mainLoop@ for (i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)

            for (equipType in EntityEquipmentSlot.values()) if (equipType.slotType == ARMOR)
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
        val phantom = ItemNBTHelper.getBoolean(armorCopy, PHANTOM_TAG, false)
        val camo = ItemStack(ItemNBTHelper.getCompound(armorCopy, PHANTOM_ITEM_TAG) ?: NBTTagCompound()).isNotEmpty

        if (phantom)
            ItemNBTHelper.removeEntry(armorCopy, PHANTOM_TAG)
        else
            ItemNBTHelper.setBoolean(armorCopy, PHANTOM_TAG, true)

        if (phantom && camo)
            ItemNBTHelper.removeEntry(armorCopy, PHANTOM_ITEM_TAG)

        val tag = armorCopy.tagCompound
        if (tag != null && tag.size == 0)
            armorCopy.tagCompound = null
        return armorCopy
    }

    override fun getRecipeOutput(): ItemStack = ItemStack.EMPTY

    override fun matches(inv: InventoryCrafting, worldIn: World?): Boolean {
        var armor: ItemStack = ItemStack.EMPTY
        var thread: ItemStack = ItemStack.EMPTY

        mainLoop@ for (i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)

            for (equipType in EntityEquipmentSlot.values()) if (equipType.slotType == ARMOR)
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

        val camo = ItemStack(ItemNBTHelper.getCompound(armor, PHANTOM_ITEM_TAG) ?: NBTTagCompound()).isNotEmpty

        return (camo || thread.isNotEmpty) && armor.isNotEmpty
    }

    override fun isHidden() = true
}

object PhantomCamoRecipe : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        registryName = ResourceLocation(LibMisc.MOD_ID, "phantom_camo")
    }

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        val ret = NonNullList.withSize(inv.sizeInventory, ItemStack.EMPTY)
        for (i in ret.indices) {
            val stack = inv.getStackInSlot(i)
            val phantom = ItemNBTHelper.getBoolean(stack, PHANTOM_TAG, false)
            val armor = EntityEquipmentSlot.values().any { it.slotType == ARMOR && stack.item.isValidArmor(stack, it, null) }
            if (phantom) {
                val container = ItemStack(ItemNBTHelper.getCompound(stack, PHANTOM_ITEM_TAG) ?: NBTTagCompound())
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

            for (equipType in EntityEquipmentSlot.values()) if (equipType.slotType == ARMOR)
                if (stack.item.isValidArmor(stack, equipType, null)) {
                    val phantom = ItemNBTHelper.getBoolean(stack, PHANTOM_TAG, false)
                    if (phantom) {
                        if (threaded.isNotEmpty)
                            return ItemStack.EMPTY
                        threaded = stack
                        threadedTypes.addAll(EntityEquipmentSlot.values().filter { it.slotType == ARMOR && stack.item.isValidArmor(stack, it, null) })
                    } else {
                        if (armor.isNotEmpty)
                            return ItemStack.EMPTY
                        armor = stack
                    }

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

        if (threadedTypes.none { armor.item.isValidArmor(armor, it, null) })
            return ItemStack.EMPTY

        val armorCopy = threaded.copy()
        ItemNBTHelper.setCompound(armorCopy, PHANTOM_ITEM_TAG, armor.writeToNBT(NBTTagCompound()))
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

            for (equipType in EntityEquipmentSlot.values()) if (equipType.slotType == ARMOR)
                if (stack.item.isValidArmor(stack, equipType, null)) {
                    val phantom = ItemNBTHelper.getBoolean(stack, PHANTOM_TAG, false)
                    if (phantom) {
                        if (threaded.isNotEmpty)
                            return false
                        threaded = stack
                        threadedTypes.addAll(EntityEquipmentSlot.values().filter { it.slotType == ARMOR && stack.item.isValidArmor(stack, it, null) })
                    } else {
                        if (armor.isNotEmpty)
                            return false
                        armor = stack
                    }
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

        if (threadedTypes.none { armor.item.isValidArmor(armor, it, null) })
            return false

        return armor.isNotEmpty && threaded.isNotEmpty
    }

    override fun isHidden() = true
}
