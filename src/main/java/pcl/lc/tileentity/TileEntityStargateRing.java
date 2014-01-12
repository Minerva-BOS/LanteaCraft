package pcl.lc.tileentity;

import pcl.common.base.GenericTileEntity;
import pcl.lc.multiblock.StargatePart;

public class TileEntityStargateRing extends GenericTileEntity {
	private StargatePart thisPart = new StargatePart(this);

	public StargatePart getAsPart() {
		return thisPart;
	}

	@Override
	public boolean canUpdate() {
		return (thisPart.getType() == null && field_145850_b != null && !field_145850_b.isRemote);
	}

	@Override
	public void func_145845_h() {
		if (thisPart.getType() == null)
			flagDirty();
	}

	public void hostBlockPlaced() {
		if (!field_145850_b.isRemote)
			flagDirty();
	}

	public void hostBlockDestroyed() {
		if (!field_145850_b.isRemote)
			flagDirty();
	}

	public void flagDirty() {
		if (thisPart.getType() == null) {
			int ord = (field_145850_b.getBlockMetadata(field_145851_c, field_145848_d, field_145849_e) & 0x1);
			thisPart.setType((ord == 0) ? "partStargateBlock" : "partStargateChevron");
		}
		thisPart.devalidateHostMultiblock();
	}

}
