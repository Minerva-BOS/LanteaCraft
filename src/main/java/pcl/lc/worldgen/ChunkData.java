package pcl.lc.worldgen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.logging.Level;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import pcl.common.helpers.ConfigValue;
import pcl.lc.LanteaCraft;
import pcl.lc.core.OreTypes;

public class ChunkData {

	private static WeakHashMap<Chunk, ChunkData> chunkCache = new WeakHashMap<Chunk, ChunkData>();

	public static ChunkData forChunk(Chunk chunk) {
		ChunkData data = chunkCache.get(chunk);
		if (data == null) {
			data = new ChunkData();
			chunkCache.put(chunk, data);
		}
		return data;
	}

	public static void onChunkLoad(ChunkDataEvent.Load e) {
		Chunk chunk = e.getChunk();
		ChunkData data = ChunkData.forChunk(chunk);
		data.readFromNBT(e.getData());
		if (((ConfigValue<Boolean>) LanteaCraft.getProxy().getConfigValue("addOresToExistingWorlds")).getValue()) {
			NaquadahOreWorldGen gen = LanteaCraft.getProxy().getOreGenerator();
			if (gen != null)
				gen.readChunk(data, chunk);
		}
	}

	public static void onChunkSave(ChunkDataEvent.Save e) {
		Chunk chunk = e.getChunk();
		ChunkData data = ChunkData.forChunk(chunk);
		data.writeToNBT(e.getData());
	}

	public static void flush() {
		chunkCache.clear();
	}

	private ArrayList<OreTypes> generatedOres;

	private ChunkData() {
		generatedOres = new ArrayList<OreTypes>();
	}

	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("LanteaCraftMeta")) {
			NBTTagCompound lcCompound = nbt.getCompoundTag("LanteaCraftMeta");
			NBTTagIntArray lcOres = (NBTTagIntArray) lcCompound.getTag("OreGenList");
			if (lcOres != null) {
				LanteaCraft.getLogger().log(Level.INFO,
						String.format("Reading %s ore generation patterns", lcOres.intArray.length));
				generatedOres.clear();
				for (int element : lcOres.intArray)
					generatedOres.add(OreTypes.fromOrdinal(element));
			}
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound lcCompound = new NBTTagCompound("LanteaCraftMeta");
		NBTTagIntArray lcOres = new NBTTagIntArray("OreGenList", new int[generatedOres.size()]);
		Iterator<OreTypes> k = generatedOres.iterator();
		LanteaCraft.getLogger().log(Level.INFO,
				String.format("Saving %s ore generation patterns", generatedOres.size()));
		for (int j = 0; k.hasNext(); j++)
			lcOres.intArray[j] = k.next().ordinal();
		lcCompound.setTag("OreGenList", lcOres);
		nbt.setTag("LanteaCraftMeta", lcCompound);
	}

	public void markOreGenerated(OreTypes typeof) {
		if (!generatedOres.contains(typeof))
			generatedOres.add(typeof);
	}

	public boolean getOreGenerated(OreTypes typeof) {
		return generatedOres.contains(typeof);
	}

}
