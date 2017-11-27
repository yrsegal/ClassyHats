package wiresegal.classy.hats.capability.data

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import wiresegal.classy.hats.capability.CapabilityHatContainer

class ClassyHatProvider(private val storage: IHatContainer = CapabilityHatContainer()) : ICapabilitySerializable<NBTTagCompound> {
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?)
            = if (CapabilityHatContainer.CAPABILITY == capability) CapabilityHatContainer.CAPABILITY.cast<T>(storage) else null

    override fun serializeNBT(): NBTTagCompound = storage.serializeNBT()

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) = capability == CapabilityHatContainer.CAPABILITY

    override fun deserializeNBT(nbt: NBTTagCompound) = storage.deserializeNBT(nbt)
}