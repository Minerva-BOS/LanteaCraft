package gcewing.sg.tileentity;

import gcewing.sg.base.GenericTileEntity;
import gcewing.sg.blocks.BlockStargateController;
import gcewing.sg.config.ConfigurationHelper;
import gcewing.sg.core.StargateNetworkChannel;
import gcewing.sg.util.Trans3;
import gcewing.sg.util.Vector3;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;

public class TileEntityStargateController extends GenericTileEntity{

	public static int linkRangeX = 10; // either side
	public static int linkRangeY = 10; // up or down
	public static int linkRangeZ = 10; // in front

	public boolean isLinkedToStargate;
	public int linkedX, linkedY, linkedZ;

	public static void configure(ConfigurationHelper cfg) {
		linkRangeX = cfg.getInteger("dhd", "linkRangeX", linkRangeX);
		linkRangeY = cfg.getInteger("dhd", "linkRangeY", linkRangeY);
		linkRangeZ = cfg.getInteger("dhd", "linkRangeZ", linkRangeZ);
	}

	public BlockStargateController getBlock() {
		return (BlockStargateController) getBlockType();
	}

	public Trans3 localToGlobalTransformation() {
		return getBlock().localToGlobalTransformation(xCoord, yCoord, zCoord, getBlockMetadata(), this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		isLinkedToStargate = nbt.getBoolean("isLinkedToStargate");
		linkedX = nbt.getInteger("linkedX");
		linkedY = nbt.getInteger("linkedY");
		linkedZ = nbt.getInteger("linkedZ");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("isLinkedToStargate", isLinkedToStargate);
		nbt.setInteger("linkedX", linkedX);
		nbt.setInteger("linkedY", linkedY);
		nbt.setInteger("linkedZ", linkedZ);
	}

	public TileEntityStargateBase getLinkedStargateTE() {
		if (isLinkedToStargate) {
			TileEntity gte = worldObj.getBlockTileEntity(linkedX, linkedY, linkedZ);
			if (gte instanceof TileEntityStargateBase)
				return (TileEntityStargateBase) gte;
		}
		return null;
	}

	public void checkForLink() {
		// System.out.printf("SGControllerTE.checkForLink at (%d,%d,%d): %s\n",
		// xCoord, yCoord, zCoord, this);
		// System.out.printf("SGControllerTE.checkForLink: isLinkedToStargate = %s\n",
		// isLinkedToStargate);
		if (!isLinkedToStargate) {
			Trans3 t = localToGlobalTransformation();
			for (int i = -linkRangeX; i <= linkRangeX; i++)
				for (int j = -linkRangeY; j <= linkRangeY; j++)
					for (int k = 1; k <= linkRangeZ; k++) {
						// for (int k = -linkRangeZ; k <= linkRangeZ; k++) {
						Vector3 p = t.p(i, j, -k);
						// System.out.printf("SGControllerTE: Looking for stargate at (%d,%d,%d)\n",
						// p.floorX(), p.floorY(), p.floorZ());
						TileEntity te = worldObj.getBlockTileEntity(p.floorX(), p.floorY(), p.floorZ());
						if (te instanceof TileEntityStargateBase)
							// System.out.printf("SGControllerTE: Found stargate at (%d,%d,%d)\n",
							// te.xCoord, te.yCoord, te.zCoord);
							if (linkToStargate((TileEntityStargateBase) te))
								return;
					}
		}
	}

	boolean linkToStargate(TileEntityStargateBase gte) {
		if (!isLinkedToStargate && !gte.isLinkedToController && gte.isMerged) {
			// System.out.printf(
			// "SGControllerTE: Linking controller at (%d, %d, %d) with stargate at (%d, %d, %d)\n",
			// xCoord, yCoord, zCoord, gte.xCoord, gte.yCoord, gte.zCoord);
			linkedX = gte.xCoord;
			linkedY = gte.yCoord;
			linkedZ = gte.zCoord;
			isLinkedToStargate = true;
			markBlockForUpdate();
			gte.linkedX = xCoord;
			gte.linkedY = yCoord;
			gte.linkedZ = zCoord;
			gte.isLinkedToController = true;
			gte.markBlockForUpdate();
			return true;
		}
		return false;
	}

	public void clearLinkToStargate() {
		// System.out.printf("SGControllerTE: Unlinking controller at (%d, %d, %d) from stargate\n",
		// xCoord, yCoord, zCoord);
		isLinkedToStargate = false;
		markBlockForUpdate();
	}
}