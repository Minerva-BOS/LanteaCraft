package pcl.lc.core;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import pcl.common.base.TickHandler;
import pcl.common.helpers.VersionHelper;
import pcl.lc.LanteaCraft;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler extends TickHandler {

	private boolean messageSent;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.PLAYER))) {
			VersionHelper versioning = LanteaCraft.getProxy().getVersionHelper();
			if (versioning.finished && !messageSent) {
				if (versioning.requiresNotify) {
					EntityPlayer player = (EntityPlayer) tickData[0];
					player.sendChatToPlayer(new ChatMessageComponent().addText("LanteaCraft "
							+ versioning.remoteVersion + "-" + versioning.remoteBuild + " is available: "
							+ versioning.remoteLabel));
				}
				messageSent = true;
			}
		}

		if (type.equals(EnumSet.of(TickType.WORLD))) {
			updateChildren();
			tickChildren();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub

	}

	@Override
	public EnumSet<TickType> ticks() {
		if (messageSent)
			return EnumSet.of(TickType.WORLD);
		else
			return EnumSet.of(TickType.WORLD, TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "LanteaCraft Server Worker";
	}
}
