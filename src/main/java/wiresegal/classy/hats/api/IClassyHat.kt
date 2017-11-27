package wiresegal.classy.hats.api

import net.minecraft.client.renderer.block.model.ModelResourceLocation

interface IClassyHat {
    val getUnlocalizedName : String

    val getModelPath : ModelResourceLocation

    val isElusive : Boolean
}