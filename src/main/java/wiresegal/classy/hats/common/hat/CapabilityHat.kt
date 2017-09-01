package wiresegal.classy.hats.common.hat

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.Capability.IStorage
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import wiresegal.classy.hats.common.hat.CapabilityHat.CAPABILITY_HAT


/**
 * @author WireSegal
 * Created at 11:35 AM on 9/1/17.
 */

object CapabilityHat {
    @CapabilityInject(IHatStorage::class)
    lateinit var CAPABILITY_HAT: Capability<IHatStorage>
}

class CapabilityHatsStorage : IStorage<IHatStorage> {

    override fun writeNBT(capability: Capability<IHatStorage>, instance: IHatStorage, side: EnumFacing): NBTBase {
        return instance.serializeNBT()
    }

    override fun readNBT(capability: Capability<IHatStorage>, instance: IHatStorage, side: EnumFacing, nbt: NBTBase) {
        instance.deserializeNBT(nbt as? NBTTagCompound ?: NBTTagCompound())
    }
}

class HatStorageProvider(private val storage: IHatStorage = BaseHatStorage()) : ICapabilitySerializable<NBTTagCompound> {
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?)
            = if (capability == CAPABILITY_HAT) CAPABILITY_HAT.cast<T>(storage) else null
    override fun serializeNBT(): NBTTagCompound
            = storage.serializeNBT()
    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?)
            = capability == CAPABILITY_HAT
    override fun deserializeNBT(nbt: NBTTagCompound)
            = storage.deserializeNBT(nbt)
}
