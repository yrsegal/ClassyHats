package wiresegal.classyhats.capability.data

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.Capability.IStorage


class ClassyHatStorage : IStorage<IHatContainer> {
    override fun writeNBT(capability: Capability<IHatContainer>, instance: IHatContainer, side: EnumFacing): NBTBase {
        return instance.serializeNBT()
    }

    override fun readNBT(capability: Capability<IHatContainer>, instance: IHatContainer, side: EnumFacing, nbt: NBTBase) {
        instance.deserializeNBT(nbt as? NBTTagCompound ?: NBTTagCompound())
    }
}

