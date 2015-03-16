package com.sanjay900.wonderland.utils;
import java.util.Random;

import net.minecraft.server.v1_8_R2.Block;
import net.minecraft.server.v1_8_R2.BlockDirt;
import net.minecraft.server.v1_8_R2.BlockDirt.EnumDirtVariant;
import net.minecraft.server.v1_8_R2.IBlockData;
import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.Blocks;
import net.minecraft.server.v1_8_R2.IBlockFragilePlantElement;
import net.minecraft.server.v1_8_R2.Item;
import net.minecraft.server.v1_8_R2.MinecraftKey;
import net.minecraft.server.v1_8_R2.World;

public class BlockGrass extends net.minecraft.server.v1_8_R2.BlockGrass implements IBlockFragilePlantElement {

    private Block backupOriginal;
    
    @Override
    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {

    }
    
    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Blocks.DIRT.getDropType(Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, EnumDirtVariant.DIRT), random, i);
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
       
    }
    public void hook() { //net.minecraft.server.v1_7_R1.Block#119
        backupOriginal = net.minecraft.server.v1_8_R2.Block.getById(2);
        this.c(0.6F);
        this.a(h);
        this.c("grass");
        net.minecraft.server.v1_8_R2.Block.REGISTRY.a(2, new MinecraftKey("grass"), this.c("grass")); //Override default sponge with our proxy
    }

    public void unhook() {
        net.minecraft.server.v1_8_R2.Block.REGISTRY.a(2, new MinecraftKey("grass"), backupOriginal); //Good thing we made a backup ;)
        backupOriginal = null; //memory and stuff
    }
}
