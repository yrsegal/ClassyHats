package wiresegal.classy.hats.network

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import wiresegal.classy.hats.capability.CapabilityHatContainer

/**
 * @author WireSegal
 * Created at 12:11 PM on 9/1/17.
 */
@PacketRegister(Side.CLIENT)
class PacketHatSync(
        @Save private var entity: Int = -1,
        @Save private var stack: ItemStack = ItemStack.EMPTY,
        @Save private var extraData: NBTTagCompound? = null
) : PacketBase() {
    override fun handle(ctx: MessageContext) {
        val player = LibrarianLib.PROXY.getClientPlayer().world.getEntityByID(entity)
        if (player is EntityPlayer) {
            val data = CapabilityHatContainer.getCapability(player)
            if (extraData?.let { data.deserializeNBT(it) } == null)
                data.equipped = stack
        }
    }
}
