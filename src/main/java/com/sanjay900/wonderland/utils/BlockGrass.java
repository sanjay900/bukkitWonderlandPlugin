package com.sanjay900.wonderland.utils;
import java.util.Random;

import net.minecraft.server.v1_8_R1.BlockDirt;
import net.minecraft.server.v1_8_R1.EnumDirtVariant;
import net.minecraft.server.v1_8_R1.IBlockData;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.Blocks;
import net.minecraft.server.v1_8_R1.IBlockFragilePlantElement;
import net.minecraft.server.v1_8_R1.Item;
import net.minecraft.server.v1_8_R1.World;

public class BlockGrass extends net.minecraft.server.v1_8_R1.BlockGrass implements IBlockFragilePlantElement {

    private Object backupOriginal;
    
    @Override
    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {

    }
    
    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Blocks.DIRT.getDropType(Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, EnumDirtVariant.DIRT), random, i);
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
       
    }
    public void hook() { //net.minecraft.server.v1_7_R1.Block#119
        backupOriginal = net.minecraft.server.v1_8_R1.Block.getById(2);
        this.c(0.6F);
        this.a(h);
        this.c("grass");
        net.minecraft.server.v1_8_R1.Block.REGISTRY.a(2, "grass", this.c("grass")); //Override default sponge with our proxy
    }

    public void unhook() {
        net.minecraft.server.v1_8_R1.Block.REGISTRY.a(2, "grass", backupOriginal); //Good thing we made a backup ;)
        backupOriginal = null; //memory and stuff
    }
}
