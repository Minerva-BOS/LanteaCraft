package pcl.lc.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockOre;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Items;

public class BlockNaquadahOre extends BlockOre {

	public BlockNaquadahOre() {
		func_149711_c(5.0F);
		func_149752_b(10.0F);
		// MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 3); ??
		func_149647_a(LanteaCraft.getCreativeTab());
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String func_149641_N() {
		return LanteaCraft.getAssetKey() + ":" + func_149739_a() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	@Override
	public Item func_149650_a(int par1, Random random, int par3) {
		return Items.naquadah;
	}

	@Override
	public int func_149745_a(Random random) {
		return 2 + random.nextInt(5);
	}
}
