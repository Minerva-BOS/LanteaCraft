package pcl.lc.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import pcl.lc.module.ModuleDecor.Blocks;

public class ItemLanteaDecor extends ItemBlock {

	public ItemLanteaDecor(int id) {
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public Icon getIconFromDamage(int i) {
		return Blocks.decorBlock.getIcon(0, i);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "tile.lanteaDecor." + stack.getItemDamage();
	}

}
