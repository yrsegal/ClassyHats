package wiresegal.classy.hats.common.core

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.kotlin.hasNullSignature
import com.teamwizardry.librarianlib.features.kotlin.writeNonnullSignature
import com.teamwizardry.librarianlib.features.kotlin.writeNullSignature
import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.saving.Save
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

/**
 * @author WireSegal
 * Created at 12:11 PM on 9/1/17.
 */
@PacketRegister(Side.CLIENT)
class PacketHatSync(@Save var stack: ItemStack = ItemStack.EMPTY, var extraData: List<ItemStack>? = null) : PacketBase() {
    override fun handle(ctx: MessageContext) {
        val clientPlayer = LibrarianLib.PROXY.getClientPlayer()
        val data = AttachmentHandler.getCapability(clientPlayer)
        data.equipped = stack
        extraData?.let { data.hats = it }
    }

    override fun readCustomBytes(buf: ByteBuf) {
        val nullSig = buf.hasNullSignature()
        val packetBuf = PacketBuffer(buf)
        if (!nullSig) {
            val size = buf.readInt()
            extraData = List(size) { packetBuf.readItemStack() }
        }
    }

    override fun writeCustomBytes(buf: ByteBuf) {
        val extra = extraData
        val packetBuf = PacketBuffer(buf)
        if (extra == null)
            buf.writeNullSignature()
        else {
            buf.writeNonnullSignature()
            buf.writeInt(extra.size)
            for (data in extra)
                packetBuf.writeItemStack(data)
        }
    }
}
