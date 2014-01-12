package pcl.lc.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pcl.common.base.GenericContainerBlock;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;
import pcl.lc.items.ItemStargateRing;
import pcl.lc.tileentity.TileEntityStargateRing;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStargateRing extends GenericContainerBlock {

	static final int numSubBlocks = 2;
	public static final int subBlockMask = 0x1;
	IIcon topAndBottomTexture;
	IIcon sideTextures[] = new IIcon[numSubBlocks];

	static String[] subBlockTitles = { "Stargate Ring Block", "Stargate Chevron Block", };

	public BlockStargateRing(int id) {
		super(id, Block.blocksList[4].blockMaterial);
		func_149711_c(1.5F);
		func_149647_a(LanteaCraft.getCreativeTab());
		registerSubItemNames();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String func_149641_N() {
		return LanteaCraft.getAssetKey() + ":" + func_149739_a() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	@Override
	public int func_149645_b() {
		if (LanteaCraft.Render.blockRingRenderer != null)
			return LanteaCraft.Render.blockRingRenderer.renderID;
		return -9001;
	}

	@Override
	public void func_149651_a(IIconRegister reg) {
		topAndBottomTexture = getIcon(reg, "stargateBlock_" + LanteaCraft.getProxy().getRenderMode());
		sideTextures[0] = getIcon(reg, "stargateRing_" + LanteaCraft.getProxy().getRenderMode());
		sideTextures[1] = getIcon(reg, "stargateChevron_" + LanteaCraft.getProxy().getRenderMode());
	}

	@Override
	public boolean func_149662_c() {
		return false;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}

	@Override
	public int func_149692_a(int data) {
		return data;
	}

	@Override
	public boolean func_149727_a(World world, int x, int y, int z, EntityPlayer player, int side, float cx, float cy, float cz) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		if (te.getAsPart().isMerged()) {
			Vector3 base = te.getAsPart().findHostMultiblock(false).getLocation();
			Block block = Block.blocksList[world.getBlockId(base.floorX(), base.floorY(), base.floorZ())];
			if (block instanceof BlockStargateBase)
				block.func_149727_a(world, base.floorX(), base.floorY(), base.floorZ(), player, side, cx, cy, cz);
			return true;
		}
		return false;
	}

	@Override
	public IIcon func_149691_a(int side, int data) {
		if (side <= 1)
			return topAndBottomTexture;
		else
			return sideTextures[data & subBlockMask];
	}

	@Override
	public void getSubBlocks(int itemID, CreativeTabs tab, List list) {
		for (int i = 0; i < numSubBlocks; i++)
			list.add(new ItemStack(itemID, 1, i));
	}

	void registerSubItemNames() {
		LanguageRegistry registry = LanguageRegistry.instance();
		for (int i = 0; i < BlockStargateRing.numSubBlocks; i++) {
			String name = ItemStargateRing.subItemName(i) + ".name";
			String title = subBlockTitles[i];
		}
	}

	@Override
	public void func_149726_b(World world, int x, int y, int z) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		te.hostBlockPlaced();
	}

	@Override
	public void func_149749_a(World world, int x, int y, int z, Block block, int data) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		super.func_149749_a(world, x, y, z, block, data);
		if (te != null)
			te.getAsPart().devalidateHostMultiblock();
	}

	@Override
	public TileEntity func_149915_a(World world, int metadata) {
		return new TileEntityStargateRing();
	}

}
