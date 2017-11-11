package wiresegal.classy.hats.common.core

import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.test.cap.CapabilityTest.Companion.cap
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.EntityList
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import sun.audio.AudioPlayer.player
import wiresegal.classy.hats.common.hat.ItemHat

/**
 * @author WireSegal
 * Created at 9:43 PM on 11/7/17.
 */
object CommandChangeHat : CommandBase() {
    override fun getName() = "replacehat"

    override fun getRequiredPermissionLevel() = 2

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.size < 2)
            throw WrongUsageException(getUsage())
        val type = args[1]
        if (type == "equipped") {
            if (args.size < 3)
                throw WrongUsageException(getUsage("equipped"))
            val player = getEntity(server, sender, args[0])
            val hatId = args[2]
            val stack = if (hatId != "none")
                ItemHat.ofHat(hatId)
            else
                ItemStack.EMPTY
            if (player is EntityPlayerMP) {
                val cap = AttachmentHandler.getCapability(player)
                if (ItemHat.getHat(cap.equipped) == ItemHat.getHat(stack))
                    throw CommandException("commands.replaceitem.failed", "equipped", 1, if (stack.isEmpty) "Air" else stack.textComponent)
                else {
                    cap.equipped = stack
                    notifyCommandListener(sender, this, "commands.replaceitem.success", "equipped", 1, if (stack.isEmpty) "Air" else stack.textComponent)
                    AttachmentHandler.syncDataFor(player, player)
                }
            } else if (EntityList.getKey(player).toString() in HatConfigHandler.names && player is EntityLiving) {
                val prev = player.entityData.getString(AttachmentHandler.customKey)

                if (prev == hatId)
                    throw CommandException("commands.replaceitem.failed", "equipped", 1, if (stack.isEmpty) "Air" else stack.textComponent)
                else {
                    player.readFromNBT(player.writeToNBT(NBTTagCompound()))
                    if (hatId == "none")
                        player.entityData.setString(AttachmentHandler.customKey, "")
                    else
                        player.entityData.setString(AttachmentHandler.customKey, hatId)
                    notifyCommandListener(sender, this, "commands.replaceitem.success", "equipped", 1, if (stack.isEmpty) "Air" else stack.textComponent)
                }
            } else
                    throw CommandException("commands.replaceitem.failed", "equipped", 1, if (stack.isEmpty) "Air" else stack.textComponent)
        } else if (type == "page") {
            if (args.size < 4)
                throw WrongUsageException(getUsage("page"))

            val player = getPlayer(server, sender, args[0])
            val cap = AttachmentHandler.getCapability(player)
            val match = "page\\.(\\d+)\\.(\\d+)".toRegex().matchEntire(args[2])
                    ?: throw CommandException(getUsage("invalid.page"), args[2])
            val hatId = args[3]

            val hatPage = match.groupValues[1].toInt()
            val hatIdx = match.groupValues[2].toInt()
            if (hatPage !in 0 until 20 || hatIdx !in 0 until 50)
                throw CommandException(getUsage("invalid.page"), args[2])
            val pos = hatPage * 50 + hatIdx

            val stack = if (hatId != "none")
                ItemHat.ofHat(hatId)
            else
                ItemStack.EMPTY
            if (ItemHat.getHat(cap.hats.getStackInSlot(pos)) == ItemHat.getHat(stack))
                throw CommandException("commands.replaceitem.failed", args[2], 1, if (stack.isEmpty) "Air" else stack.textComponent)
            else {
                cap.hats.setStackInSlot(pos, stack)
                notifyCommandListener(sender, this, "commands.replaceitem.success", args[2], 1, if (stack.isEmpty) "Air" else stack.textComponent)
                AttachmentHandler.syncDataFor(player, player)
            }
        } else throw WrongUsageException(getUsage())
    }

    fun getUsage(key: String? = null) = "classyhats.replacehat.usage" + if (key == null) "" else ".$key"

    override fun getUsage(sender: ICommandSender?) = getUsage()

    val pages = (0 until 20).flatMap { k -> (0 until 50).map { "page.$k.$it" } }

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<String>, targetPos: BlockPos?): List<String> {
        if (args.size == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, *server.onlinePlayerNames)
        else if (args.size == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, "equipped", "page")
        else {
            if (args[1] == "equipped") {
                if (args.size == 3)
                    return CommandBase.getListOfStringsMatchingLastWord(args, "none", *HatConfigHandler.hats.keys.toTypedArray())
            } else if (args[1] == "page") {
                if (args.size == 3)
                    return CommandBase.getListOfStringsMatchingLastWord(args, pages)
                else if (args.size == 4)
                    return CommandBase.getListOfStringsMatchingLastWord(args, "none", *HatConfigHandler.hats.keys.toTypedArray())
            }
        }
        return emptyList()
    }

    override fun isUsernameIndex(args: Array<String>, index: Int) = index == 0
}
