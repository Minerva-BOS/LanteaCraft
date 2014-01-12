package pcl.lc.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.lc.LanteaCraft;
import net.minecraft.block.BlockOre;

public class BlockNaquadah extends BlockOre {
	public BlockNaquadah() {
		super();
		func_149711_c(5.0F);
		func_149752_b(10.0F);
		// func_149672_a(soundMetaFlFootstep); soundMetaFlFootstep??
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String func_149641_N() {
		return LanteaCraft.getAssetKey() + ":" + func_149739_a() + "_" + LanteaCraft.getProxy().getRenderMode();
	}
}
