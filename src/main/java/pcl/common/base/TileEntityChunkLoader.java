package pcl.common.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public abstract class TileEntityChunkLoader extends GenericTileEntity {

	public abstract TileEntityChunkManager getChunkManager();

	Ticket chunkTicket;

	public void setForcedChunkRange(int minX, int minZ, int maxX, int maxZ) {
		releaseChunkTicket();
		Ticket ticket = getChunkTicket();
		if (ticket != null) {
			NBTTagCompound nbt = ticket.getModData();
			nbt.setString("type", "TileEntity");
			nbt.setInteger("xCoord", field_145851_c);
			nbt.setInteger("yCoord", field_145848_d);
			nbt.setInteger("zCoord", field_145849_e);
			nbt.setInteger("rangeMinX", minX);
			nbt.setInteger("rangeMinZ", minZ);
			nbt.setInteger("rangeMaxX", maxX);
			nbt.setInteger("rangeMaxZ", maxZ);
			forceChunkRangeOnTicket(ticket);
		}
	}

	public void clearForcedChunkRange() {
		releaseChunkTicket();
	}

	public void forceChunkRangeOnTicket(Ticket ticket) {
		NBTTagCompound nbt = ticket.getModData();
		int minX = nbt.getInteger("rangeMinX");
		int minZ = nbt.getInteger("rangeMinZ");
		int maxX = nbt.getInteger("rangeMaxX");
		int maxZ = nbt.getInteger("rangeMaxZ");
		int chunkX = field_145851_c >> 4;
		int chunkZ = field_145849_e >> 4;
		for (int i = minX; i <= maxX; i++)
			for (int j = minZ; j <= maxZ; j++) {
				int x = chunkX + i, z = chunkZ + j;
				// System.out.printf("BaseChunkLoadingTE.forceChunkRangeOnTicket: forcing chunk (%d; %d, %d)\n",
				// worldObj.provider.dimensionId, x, z);
				ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(x, z));
			}
	}

	public Ticket getChunkTicket() {
		if (chunkTicket == null)
			chunkTicket = getChunkManager().newTicket(field_145850_b);
		return chunkTicket;
	}

	public boolean reinstateChunkTicket(Ticket ticket) {
		if (chunkTicket == null) {
			chunkTicket = ticket;
			forceChunkRangeOnTicket(ticket);
			return true;
		} else
			return false;
	}

	@Override
	public void func_145843_s() {
		// System.out.printf("BaseChunkLoadingTE.invalidate\n");
		releaseChunkTicket();
		super.func_145843_s();
	}

	public void releaseChunkTicket() {
		if (chunkTicket != null) {
			ForgeChunkManager.releaseTicket(chunkTicket);
			chunkTicket = null;
		}
	}
}
