package wiresegal.classy.hats.network

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import wiresegal.classy.hats.capability.CapabilityHatContainer


/**
 * @author WireSegal
 * Created at 4:31 PM on 9/1/17.
 */
@PacketRegister(Side.SERVER)
class PacketSyncLastSelectedSection(@Save private var target: Int = 0) : PacketBase() {
    override fun handle(ctx: MessageContext) {
        val player = ctx.serverHandler.player
        CapabilityHatContainer.getCapability(player).currentHatSection = target
    }
}
