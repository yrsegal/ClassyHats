package wiresegal.classy.hats.common.core

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

/**
 * @author WireSegal
 * Created at 12:11 PM on 9/1/17.
 */
@PacketRegister(Side.CLIENT)
class PacketHatSync(@Save var entity: Int = -1, @Save var stack: ItemStack = ItemStack.EMPTY, @Save var extraData: NBTTagCompound? = null) : PacketBase() {
    override fun handle(ctx: MessageContext) {
        val player = LibrarianLib.PROXY.getClientPlayer().world.getEntityByID(entity)
        if (player is EntityPlayer) {
            val data = AttachmentHandler.getCapability(player)
            if (extraData?.let { data.deserializeNBT(it) } == null)
                data.equipped = stack
        }
    }
}
