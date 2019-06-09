package wiresegal.classyhats.command

import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.EntityList
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import wiresegal.classyhats.ClassyHatsConfig
import wiresegal.classyhats.ClassyHatsContent
import wiresegal.classyhats.capability.CapabilityHatContainer
import wiresegal.classyhats.event.CapabilityEvents

/**
 * @author WireSegal
 * Created at 9:43 PM on 11/7/17.
 */
object CommandChangeHat : CommandBase() {
    override fun getName() = "replacehat"

    override fun getRequiredPermissionLevel() = 2

    private val msgFail = "commands.replaceitem.failed"
    private val msgSuccess = "commands.replaceitem.success"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<out String>) {
        if (args.size < 2) throw WrongUsageException(getUsage())
        when (args[1]) {
            "equipped" -> {
                if (args.size < 3) throw WrongUsageException(getUsage("equipped"))
                val player = getEntity(server, sender, args[0])
                val hatId = args[2]
                val stack = if (hatId != "none") ClassyHatsContent.HAT.ofHat(hatId) else ItemStack.EMPTY
                if (player is EntityPlayerMP) {
                    val cap = CapabilityHatContainer.getCapability(player)
                    if (ClassyHatsContent.HAT.getHat(cap.equipped) == ClassyHatsContent.HAT.getHat(stack))
                        throw CommandException(msgFail, "equipped", 1, if (stack.isEmpty) "Air" else stack.textComponent)
                    else {
                        cap.equipped = stack
                        notifyCommandListener(sender, this, msgSuccess, "equipped", 1, if (stack.isEmpty) "Air" else stack.textComponent)
                        CapabilityEvents.syncDataFor(player, player)
                    }
                } else if (EntityList.getKey(player).toString() in ClassyHatsConfig.names && player is EntityLiving) {
                    val prev = player.entityData.getString(CapabilityEvents.customKey)

                    if (prev == hatId)
                        throw CommandException(msgFail, "equipped", 1, if (stack.isEmpty) "Air" else stack.textComponent)
                    else {
                        player.readFromNBT(player.writeToNBT(NBTTagCompound()))
                        if (hatId == "none")
                            player.entityData.setString(CapabilityEvents.customKey, "")
                        else
                            player.entityData.setString(CapabilityEvents.customKey, hatId)
                        notifyCommandListener(sender, this, msgSuccess, "equipped", 1, if (stack.isEmpty) "Air" else stack.textComponent)
                    }
                } else
                    throw CommandException(msgFail, "equipped", 1, if (stack.isEmpty) "Air" else stack.textComponent)
            }
            "page" -> {
                if (args.size < 4)
                    throw WrongUsageException(getUsage("page"))

                val player = getPlayer(server, sender, args[0])
                val cap = CapabilityHatContainer.getCapability(player)
                val match = "page\\.(\\d+)\\.(\\d+)".toRegex().matchEntire(args[2])
                        ?: throw CommandException(getUsage("invalid.page"), args[2])
                val hatId = args[3]

                val hatPage = match.groupValues[1].toInt()
                val hatIdx = match.groupValues[2].toInt()
                if (hatPage !in 0 until 20 || hatIdx !in 0 until 50)
                    throw CommandException(getUsage("invalid.page"), args[2])
                val pos = hatPage * 50 + hatIdx

                val stack = if (hatId != "none")
                    ClassyHatsContent.HAT.ofHat(hatId)
                else
                    ItemStack.EMPTY
                if (ClassyHatsContent.HAT.getHat(cap.hats.getStackInSlot(pos)) == ClassyHatsContent.HAT.getHat(stack))
                    throw CommandException(msgFail, args[2], 1, stack.textComponent)
                else {
                    cap.hats.setStackInSlot(pos, stack)
                    notifyCommandListener(sender, this, msgSuccess, args[2], 1, stack.textComponent)
                    CapabilityEvents.syncDataFor(player, player)
                }
            }
            else -> throw WrongUsageException(getUsage())
        }
    }

    private fun getUsage(key: String? = null) = "command.classyhats.replacehat.usage" + if (key == null) "" else ".$key"

    override fun getUsage(sender: ICommandSender?) = getUsage()

    private val pages = (0 until 20).flatMap { k -> (0 until 50).map { "page.$k.$it" } }

    override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<String>, targetPos: BlockPos?): List<String> {
        when (args.size) {
            1 -> return CommandBase.getListOfStringsMatchingLastWord(args, *server.onlinePlayerNames)
            2 -> return CommandBase.getListOfStringsMatchingLastWord(args, "equipped", "page")
            else -> {
                when (args[1]) {
                    "equipped" -> if (args.size == 3)
                        return CommandBase.getListOfStringsMatchingLastWord(
                                args, "none", *ClassyHatsConfig.hats.keys.toTypedArray()
                        )
                    "page" -> when (args.size) {
                        3 -> return CommandBase.getListOfStringsMatchingLastWord(args, pages)
                        4 -> return CommandBase.getListOfStringsMatchingLastWord(
                                args, "none", *ClassyHatsConfig.hats.keys.toTypedArray()
                        )
                    }
                }
            }
        }
        return emptyList()
    }

    override fun isUsernameIndex(args: Array<String>, index: Int) = index == 0
}
