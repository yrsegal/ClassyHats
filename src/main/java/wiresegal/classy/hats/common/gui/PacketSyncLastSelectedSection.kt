package wiresegal.classy.hats.common.gui

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import wiresegal.classy.hats.common.core.AttachmentHandler


/**
 * @author WireSegal
 * Created at 4:31 PM on 9/1/17.
 */
@PacketRegister(Side.SERVER)
class PacketSyncLastSelectedSection(@Save var target: Int = 0) : PacketBase() {
    override fun handle(ctx: MessageContext) {
        val player = ctx.serverHandler.player
        AttachmentHandler.getCapability(player).currentHatSection = target
    }
}
