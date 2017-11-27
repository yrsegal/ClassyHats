package wiresegal.classy.hats.network

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import wiresegal.classy.hats.event.CapabilityEvents

/**
 * @author WireSegal
 * Created at 12:11 PM on 9/1/17.
 */
@PacketRegister(Side.CLIENT)
class PacketEntityHatSync(
        @Save private var entity: Int = -1,
        @Save private var name: String = ""
) : PacketBase() {
    override fun handle(ctx: MessageContext) {
        val entity = LibrarianLib.PROXY.getClientPlayer().world.getEntityByID(entity)
        entity?.entityData?.setString(CapabilityEvents.customKey, name)
    }
}
