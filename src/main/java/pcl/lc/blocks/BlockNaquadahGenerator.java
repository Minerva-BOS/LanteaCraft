package pcl.lc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pcl.common.base.RotationOrientedBlock;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockNaquadahGenerator extends RotationOrientedBlock {

	Icon topTexture, bottomTexture, sideTexture;

	public BlockNaquadahGenerator(int id) {
		super(id, Material.rock);
		setHardness(1.5F);
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return LanteaCraft.getAssetKey() + ":" + getUnlocalizedName() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	@Override
	public void registerIcons(IconRegister register) {
		topTexture = register.registerIcon(LanteaCraft.getAssetKey() + ":" + "controller_top_"
				+ LanteaCraft.getProxy().getRenderMode());
		bottomTexture = register.registerIcon(LanteaCraft.getAssetKey() + ":" + "controller_bottom_"
				+ LanteaCraft.getProxy().getRenderMode());
		sideTexture = register.registerIcon(LanteaCraft.getAssetKey() + ":" + "controller_side_"
				+ LanteaCraft.getProxy().getRenderMode());
	}

	@Override
	public Icon getIcon(int side, int data) {
		switch (side) {
		case 0:
			return bottomTexture;
		case 1:
			return topTexture;
		default:
			return sideTexture;
		}
	}

	@Override
	public int getRenderType() {
		if (LanteaCraft.Render.blockNaquadahGeneratorRenderer != null)
			return LanteaCraft.Render.blockNaquadahGeneratorRenderer.renderID;
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return !LanteaCraft.getProxy().isUsingModels();
	}

	@Override
	public boolean isOpaqueCube() {
		return !LanteaCraft.getProxy().isUsingModels();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, player, stack);
		int dir = MathHelper.floor_double(player.rotationYaw * 4F / 360F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 0);
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int data) {
		TileEntityNaquadahGenerator cte = (TileEntityNaquadahGenerator) getTileEntity(world, x, y, z);
		cte.onHostBlockBreak();
		super.breakBlock(world, x, y, z, id, data);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		TileEntityNaquadahGenerator te = (TileEntityNaquadahGenerator) getTileEntity(world, x, y, z);
		if (te != null) {
			player.openGui(LanteaCraft.getInstance(), LanteaCraft.EnumGUIs.NaquadahGenerator.ordinal(), world, x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityNaquadahGenerator();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int l) {
		if (!world.isRemote) {
			int sig = 0;
			int a = isBlockProvidingPower(world, x, y + 1, z, 1);
			int b = isBlockProvidingPower(world, x, y - 1, z, 0);
			int c = isBlockProvidingPower(world, x, y, z + 1, 3);
			int d = isBlockProvidingPower(world, x, y, z - 1, 2);
			int e = isBlockProvidingPower(world, x + 1, y, z, 5);
			int f = isBlockProvidingPower(world, x - 1, y, z, 4);
			sig = Math.max(a, Math.max(b, Math.max(c, Math.max(d, Math.max(e, f)))));
			TileEntityNaquadahGenerator cte = (TileEntityNaquadahGenerator) getTileEntity(world, x, y, z);
			cte.setRedstoneInputSignal(sig);
		}
	}

	private int isBlockProvidingPower(World world, int x, int y, int z, int direction) {
		if (y >= 0 && y < world.getHeight()) {
			int redstoneWireValue = (world.getBlockId(x, y, z) == Block.redstoneWire.blockID) ? world.getBlockMetadata(
					x, y, z) : 0;
			int indirectPowerTo = world.getIndirectPowerLevelTo(x, y, z, direction);
			int directPowerTo = world.isBlockProvidingPowerTo(x, y, z, direction);
			return Math.max(Math.max(redstoneWireValue, indirectPowerTo), directPowerTo);
		} else
			return 0;
	}

}
