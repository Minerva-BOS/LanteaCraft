package pcl.common.base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.lc.LanteaCraft;

public abstract class GenericContainerBlock extends BlockContainer {

	static Random random = new Random();
	String[] iconNames = null;
	IIcon[] icons;

	public GenericContainerBlock(Material material) {
		super(material);
	}

	@Override
	public boolean func_149686_d() {
		return func_149645_b() == 0;
	}

	@Deprecated
	public IIcon getIcon(IIconRegister register, String whoknows) {
		return register.registerIcon(LanteaCraft.getAssetKey() + ":" + whoknows);
	}

	@Override
	public abstract void func_149651_a(IIconRegister reg);

	@Override
	public abstract TileEntity func_149915_a(World world, int metadata);

	public TileEntity getTileEntity(IBlockAccess world, int x, int y, int z) {
		return world.func_147438_o(x, y, z);
	}

	@Override
	public abstract void func_149726_b(World world, int x, int y, int z);

	@Override
	public void func_149749_a(World world, int x, int y, int z, Block par5, int par6) {
		TileEntity te = world.func_147438_o(x, y, z);
		if (te instanceof IInventory) {
			IInventory var7 = (IInventory) te;
			if (var7 != null)
				for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
					ItemStack var9 = var7.getStackInSlot(var8);
					if (var9 != null) {
						float var10 = random.nextFloat() * 0.8F + 0.1F;
						float var11 = random.nextFloat() * 0.8F + 0.1F;
						EntityItem var14;
						for (float var12 = random.nextFloat() * 0.8F + 0.1F; var9.stackSize > 0; world.spawnEntityInWorld(var14)) {
							int var13 = random.nextInt(21) + 10;
							if (var13 > var9.stackSize)
								var13 = var9.stackSize;
							var9.stackSize -= var13;
							var14 = new EntityItem(world, x + var10, y + var11, z + var12, new ItemStack(var9.getItem(), var13,
									var9.getItemDamage()));
							float var15 = 0.05F;
							var14.motionX = (float) random.nextGaussian() * var15;
							var14.motionY = (float) random.nextGaussian() * var15 + 0.2F;
							var14.motionZ = (float) random.nextGaussian() * var15;
							if (var9.hasTagCompound())
								var14.getEntityItem().setTagCompound((NBTTagCompound) var9.getTagCompound().copy());
						}
					}
				}
		}
		super.func_149749_a(world, x, y, z, par5, par6);
	}
}
