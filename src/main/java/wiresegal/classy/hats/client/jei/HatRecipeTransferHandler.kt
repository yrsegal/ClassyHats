package wiresegal.classy.hats.client.jei

import com.google.common.collect.ImmutableSet
import mezz.jei.Internal
import mezz.jei.JustEnoughItems
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import mezz.jei.api.recipe.transfer.IRecipeTransferError
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper
import mezz.jei.config.SessionData
import mezz.jei.gui.ingredients.GuiItemStackGroup
import mezz.jei.network.packets.PacketRecipeTransfer
import mezz.jei.startup.StackHelper
import mezz.jei.transfer.BasicRecipeTransferInfo
import mezz.jei.util.Log
import mezz.jei.util.Translator
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import wiresegal.classy.hats.common.gui.ContainerHat
import java.util.*

class HatRecipeTransferHandler(private val handlerHelper: IRecipeTransferHandlerHelper) : IRecipeTransferHandler<ContainerHat> {
    private val stackHelper = Internal.getStackHelper()
    private val transferHelper = BasicRecipeTransferInfo(ContainerHat::class.java, VanillaRecipeCategoryUid.CRAFTING, 1, 4, 9, 36)

    override fun getContainerClass(): Class<ContainerHat> {
        return transferHelper.containerClass
    }

    override fun transferRecipe(container: ContainerHat, recipeLayout: IRecipeLayout, player: EntityPlayer, maxTransfer: Boolean, doTransfer: Boolean): IRecipeTransferError? {
        if (!SessionData.isJeiOnServer()) {
            val tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.no.server")
            return handlerHelper.createUserErrorWithTooltip(tooltipMessage)
        }

        if (!transferHelper.canHandle(container)) {
            return handlerHelper.createInternalError()
        }

        val inventorySlots = HashMap<Int, Slot>()
        for (slot in transferHelper.getInventorySlots(container)) {
            inventorySlots.put(slot.slotNumber, slot)
        }

        val craftingSlots = HashMap<Int, Slot>()
        for (slot in transferHelper.getRecipeSlots(container)) {
            craftingSlots.put(slot.slotNumber, slot)
        }

        val itemStackGroup = recipeLayout.itemStacks
        var inputCount = 0
        // indexes that do not fit into the player crafting grid
        val badIndexes = ImmutableSet.of(2, 5, 6, 7, 8)

        var inputIndex = 0
        for (ingredient in itemStackGroup.guiIngredients.values) {
            if (ingredient.isInput) {
                if (!ingredient.allIngredients.isEmpty()) {
                    inputCount++
                    if (badIndexes.contains(inputIndex)) {
                        val tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.too.large.player.inventory")
                        return handlerHelper.createUserErrorWithTooltip(tooltipMessage)
                    }
                }
                inputIndex++
            }
        }

        // compact the crafting grid into a 2x2 area
        val guiIngredients = itemStackGroup.guiIngredients.values.filter { it.isInput }
        val playerInvItemStackGroup = GuiItemStackGroup(null, 0)
        val playerGridIndexes = intArrayOf(0, 1, 3, 4)
        for (i in 0..3) {
            val index = playerGridIndexes[i]
            if (index < guiIngredients.size) {
                val ingredient = guiIngredients[index]
                playerInvItemStackGroup.init(i, true, 0, 0)
                playerInvItemStackGroup.set(i, ingredient.allIngredients)
            }
        }

        val availableItemStacks = HashMap<Int, ItemStack>()
        var filledCraftSlotCount = 0
        var emptySlotCount = 0

        for (slot in craftingSlots.values) {
            val stack = slot.stack
            if (!stack.isEmpty) {
                if (!slot.canTakeStack(player)) {
                    Log.get().error("Recipe Transfer helper {} does not work for container {}. Player can't move item out of Crafting Slot number {}", transferHelper.javaClass, container.javaClass, slot.slotNumber)
                    return handlerHelper.createInternalError()
                }
                filledCraftSlotCount++
                availableItemStacks.put(slot.slotNumber, stack.copy())
            }
        }

        for (slot in inventorySlots.values) {
            val stack = slot.stack
            if (!stack.isEmpty) {
                availableItemStacks.put(slot.slotNumber, stack.copy())
            } else {
                emptySlotCount++
            }
        }

        // check if we have enough inventory space to shuffle items around to their final locations
        if (filledCraftSlotCount - inputCount > emptySlotCount) {
            val message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.inventory.full")
            return handlerHelper.createUserErrorWithTooltip(message)
        }

        var matchingItemsResult: StackHelper.MatchingItemsResult = stackHelper.getMatchingItems(availableItemStacks, playerInvItemStackGroup.guiIngredients)

        if (matchingItemsResult.missingItems.size > 0) {
            val message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing")
            matchingItemsResult = stackHelper.getMatchingItems(availableItemStacks, itemStackGroup.guiIngredients)
            return handlerHelper.createUserErrorForSlots(message, matchingItemsResult.missingItems)
        }

        val craftingSlotIndexes = ArrayList(craftingSlots.keys)
        Collections.sort(craftingSlotIndexes)

        val inventorySlotIndexes = ArrayList(inventorySlots.keys)
        Collections.sort(inventorySlotIndexes)

        // check that the slots exist and can be altered
        for ((craftNumber) in matchingItemsResult.matchingItems) {
            val slotNumber = craftingSlotIndexes[craftNumber]
            if (slotNumber < 0 || slotNumber >= container.inventorySlots.size) {
                Log.get().error("Recipes Transfer Helper {} references slot {} outside of the inventory's size {}", transferHelper.javaClass, slotNumber, container.inventorySlots.size)
                return handlerHelper.createInternalError()
            }
        }

        if (doTransfer) {
            val packet = PacketRecipeTransfer(matchingItemsResult.matchingItems, craftingSlotIndexes, inventorySlotIndexes, maxTransfer)
            JustEnoughItems.getProxy().sendPacketToServer(packet)
        }

        return null
    }
}
