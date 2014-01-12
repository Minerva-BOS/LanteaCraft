package pcl.lc.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pcl.common.base.RotationOrientedBlock;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityStargateController;

public class BlockStargateController extends RotationOrientedBlock {

	IIcon topTexture, bottomTexture, sideTexture;

	public BlockStargateController(int id) {
		super(id, Material.rock);
		func_149711_c(1.5F);
		func_149647_a(LanteaCraft.getCreativeTab());
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String func_149641_N() {
		return LanteaCraft.getAssetKey() + ":" + func_149739_a() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	@Override
	public void func_149651_a(IIconRegister reg) {
		topTexture = getIcon(reg, "controller_top_" + LanteaCraft.getProxy().getRenderMode());
		bottomTexture = getIcon(reg, "controller_bottom_" + LanteaCraft.getProxy().getRenderMode());
		sideTexture = getIcon(reg, "controller_side_" + LanteaCraft.getProxy().getRenderMode());
	}

	@Override
	public IIcon func_149691_a(int side, int data) {
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
	public int func_149645_b() {
		if (LanteaCraft.Render.blockBaseRenderer != null)
			return LanteaCraft.Render.blockControllerRenderer.renderID;
		return 0;
	}

	@Override
	public boolean func_149686_d() {
		return !LanteaCraft.getProxy().isUsingModels();
	}

	@Override
	public boolean func_149662_c() {
		return !LanteaCraft.getProxy().isUsingModels();
	}

	@Override
	public void func_149689_a(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		super.func_149689_a(world, x, y, z, player, stack);
		checkForLink(world, x, y, z);
		int dir = MathHelper.floor_double(player.rotationYaw * 4F / 360F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 0);
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public boolean func_149727_a(World world, int x, int y, int z, EntityPlayer player, int side, float cx, float cy, float cz) {
		player.openGui(LanteaCraft.getInstance(), LanteaCraft.EnumGUIs.StargateController.ordinal(), world, x, y, z);
		return true;
	}

	public void checkForLink(World world, int x, int y, int z) {
		((TileEntityStargateController) getTileEntity(world, x, y, z)).checkForLink();
	}

	@Override
	public TileEntity func_149915_a(World world, int metadata) {
		return new TileEntityStargateController();
	}

	@Override
	public void func_149726_b(World world, int x, int y, int z) {
		// TODO Auto-generated method stub

	}

}
