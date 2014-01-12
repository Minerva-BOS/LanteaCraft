package pcl.lc.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.lc.tileentity.TileEntityStargateBase;

public class WorldLocation {

	public int dimension;
	public int x, y, z;

	public WorldLocation(TileEntity te) {
		this(te.field_145850_b.provider.dimensionId, te.field_145851_c, te.field_145848_d, te.field_145849_e);
	}

	public WorldLocation(int dimension, int x, int y, int z) {
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldLocation(NBTTagCompound nbt) {
		dimension = nbt.getInteger("dimension");
		x = nbt.getInteger("x");
		y = nbt.getInteger("y");
		z = nbt.getInteger("z");
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dimension", dimension);
		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);
		return nbt;
	}

	public TileEntityStargateBase getStargateTE() {
		World world = GateAddressHelper.getWorld(dimension);
		if (world == null)
			return null;
		TileEntity te = world.func_147438_o(x, y, z);
		if (te instanceof TileEntityStargateBase)
			return (TileEntityStargateBase) te;
		else
			return null;
	}

}
