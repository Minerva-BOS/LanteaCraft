package pcl.lc.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pcl.common.base.RotationOrientedBlock;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityStargateBase;

public class BlockStargateBase extends RotationOrientedBlock {
	protected IIcon topAndBottomTexture;
	protected IIcon frontTexture;
	protected IIcon sideTexture;

	public BlockStargateBase(int id) {
		super(id, Material.rock);
		func_149711_c(1.5F);
		func_149675_a(true);
		func_149647_a(LanteaCraft.getCreativeTab());
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String func_149641_N() {
		return LanteaCraft.getAssetKey() + ":" + func_149739_a() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	@Override
	public int func_149709_b(IBlockAccess world, int x, int y, int z, int blockID) {
		return TileEntityStargateBase.powerLevel;
	}

	@Override
	public int func_149748_c(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return TileEntityStargateBase.powerLevel;
	}

	@Override
	public boolean func_149744_f() {
		return true;
	}

	@Override
	public int func_149645_b() {
		if (LanteaCraft.Render.blockBaseRenderer != null)
			return LanteaCraft.Render.blockBaseRenderer.renderID;
		return -9001;
	}

	@Override
	public void func_149651_a(IIconRegister reg) {
		topAndBottomTexture = getIcon(reg, "stargateBlock_" + LanteaCraft.getProxy().getRenderMode());
		frontTexture = getIcon(reg, "stargateBase_front_" + LanteaCraft.getProxy().getRenderMode());
		sideTexture = getIcon(reg, "stargateRing_" + LanteaCraft.getProxy().getRenderMode());
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
	public void func_149689_a(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		int data = Math.round((180 - player.rotationYaw) / 90) & 3;
		world.setBlockMetadataWithNotify(x, y, z, data, 0x3);
		TileEntityStargateBase te = (TileEntityStargateBase) getTileEntity(world, x, y, z);
		te.hostBlockPlaced();
	}

	@Override
	public boolean func_149727_a(World world, int x, int y, int z, EntityPlayer player, int side, float cx, float cy, float cz) {
		TileEntityStargateBase te = (TileEntityStargateBase) getTileEntity(world, x, y, z);
		if (te != null)
			if (te.getAsStructure().isValid()) {
				player.openGui(LanteaCraft.getInstance(), LanteaCraft.EnumGUIs.StargateBase.ordinal(), world, x, y, z);
				return true;
			}
		return false;
	}

	@Override
	public IIcon func_149691_a(int side, int data) {
		if (side <= 1)
			return topAndBottomTexture;
		else if (side == 3)
			return frontTexture;
		else
			return sideTexture;
	}

	@Override
	public void func_149749_a(World world, int x, int y, int z, Block block, int data) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileEntityStargateBase)
			((TileEntityStargateBase) te).hostBlockDestroyed();
		super.func_149749_a(world, x, y, z, block, data);
	}

	void explode(World world, double x, double y, double z, double s) {
		world.newExplosion(null, x, y, z, (float) s, true, true);
	}

	@Override
	public TileEntity func_149915_a(World world, int metadata) {
		return new TileEntityStargateBase();
	}

	@Override
	public void func_149726_b(World world, int x, int y, int z) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileEntityStargateBase)
			((TileEntityStargateBase) te).getAsStructure().invalidate();
	}

	public boolean isMerged(IBlockAccess world, int x, int y, int z) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileEntityStargateBase)
			return ((TileEntityStargateBase) te).getAsStructure().isValid();
		return false;
	}
}
