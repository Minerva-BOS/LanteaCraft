package pcl.common.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class GenericTileEntity extends TileEntity implements IInventory, ISidedInventory {

	@Override
	public Packet getDescriptionPacket() {
		if (syncWithClient()) {
			NBTTagCompound nbt = new NBTTagCompound();
			writeToNBT(nbt);
			return new Packet132TileEntityData(field_145851_c, field_145848_d, field_145849_e, 0, nbt);
		} else
			return null;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
		field_145850_b.func_147471_g(field_145851_c, field_145848_d, field_145849_e);
	}

	boolean syncWithClient() {
		return true;
	}

	public void markBlockForUpdate() {
		field_145850_b.func_147471_g(field_145851_c, field_145848_d, field_145849_e);
	}

	public void playSoundEffect(String name, float volume, float pitch) {
		field_145850_b.playSoundEffect(field_145851_c + 0.5, field_145848_d + 0.5, field_145849_e + 0.5, name, volume, pitch);
	}

	public IInventory getInventory() {
		return null;
	}

	@Override
	public void func_145839_a(NBTTagCompound nbt) {
		super.func_145839_a(nbt);
		IInventory inventory = getInventory();
		if (inventory != null) {
			NBTTagList list = nbt.getTagList("inventory");
			int n = list.tagCount();
			for (int i = 0; i < n; i++) {
				NBTTagCompound item = (NBTTagCompound) list.tagAt(i);
				int slot = item.getInteger("slot");
				ItemStack stack = ItemStack.loadItemStackFromNBT(item);
				inventory.setInventorySlotContents(slot, stack);
			}
		}
	}

	@Override
	public void func_145841_b(NBTTagCompound nbt) {
		super.func_145841_b(nbt);
		IInventory inventory = getInventory();
		if (inventory != null) {
			NBTTagList list = new NBTTagList();
			int n = inventory.getSizeInventory();
			for (int i = 0; i < n; i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack != null) {
					NBTTagCompound item = new NBTTagCompound();
					item.setInteger("slot", i);
					stack.writeToNBT(item);
					list.appendTag(item);
				}
			}
			nbt.setTag("inventory", list);
		}
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getSizeInventory() : 0;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int slot) {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getStackInSlot(slot) : null;
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number
	 * (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			ItemStack result = inventory.decrStackSize(slot, amount);
			onInventoryChanged();
			return result;
		} else
			return null;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop
	 * whatever it returns as an EntityItem - like when you close a workbench
	 * GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			ItemStack result = inventory.getStackInSlotOnClosing(slot);
			onInventoryChanged();
			return result;
		} else
			return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			inventory.setInventorySlotContents(slot, stack);
			onInventoryChanged();
		}
	}

	/**
	 * Returns the name of the inventory.
	 */
	@Override
	public String func_145825_b() {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.func_145825_b() : "";
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be
	 * 64, possibly will be extended. *Isn't this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit() {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getInventoryStackLimit() : 0;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.isUseableByPlayer(player) : true;
	}

	@Override
	public void openChest() {
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.openChest();
	}

	@Override
	public void closeChest() {
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.closeChest();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		IInventory inventory = getInventory();
		if (inventory != null)
			return inventory.isItemValidForSlot(slot, stack);
		else
			return false;
	}

	@Override
	public boolean func_145818_k_() {
		IInventory inventory = getInventory();
		if (inventory != null)
			return inventory.func_145818_k_();
		else
			return false;
	}

	/**
	 * Returns an array containing the indices of the slots that can be accessed
	 * by automation on the given side of this block.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).getAccessibleSlotsFromSide(side);
		else
			return new int[0];
	}

	/**
	 * Returns true if automation can insert the given item in the given slot
	 * from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).canInsertItem(slot, stack, side);
		else
			return true;
	}

	/**
	 * Returns true if automation can extract the given item in the given slot
	 * from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).canExtractItem(slot, stack, side);
		else
			return true;
	}

}
