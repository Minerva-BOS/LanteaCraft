package pcl.lc.guis;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;

import org.lwjgl.input.Keyboard;

import pcl.common.helpers.AnalyticsHelper;
import pcl.lc.LanteaCraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiStatCollection extends GuiScreen {
	private GuiScreen parentGuiScreen;
	private AnalyticsHelper analytics;
	private String label;

	public GuiStatCollection(GuiScreen par1GuiScreen, AnalyticsHelper analytics) {
		this.parentGuiScreen = par1GuiScreen;
		this.analytics = analytics;
	}

	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		int dx = this.width / 2, dy = this.height / 4 + 120 + 24;

		buttonList.add(new GuiButton(0, dx - 180 - 5, dy, 180, 20, "Allow anonymous stats"));
		buttonList.add(new GuiButton(1, dx + 5, dy, 180, 20, "Don't allow anonymous stats"));
		try {
			label = URLDecoder.decode(analytics.getReportData(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LanteaCraft.getLogger().log(Level.WARNING, "Client doesn't support UTF-8?", e);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (button == 0) {
			int hx = this.height / 4 + 96;
			System.out.println(String.format("(%s, %s)", x, y));
		}
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			LanteaCraft.getLogger().log(Level.INFO, "Close prompt!");
			this.mc.displayGuiScreen(this.parentGuiScreen);
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		drawCenteredString(this.fontRenderer, "LanteaCraft Statistics", this.width / 2, 10, 0xF4CA16);
		fontRenderer
				.drawSplitString(
						"LanteaCraft would like permission to send the following anonymous statistics to our Metrics server - this information allows us to "
								+ "better understand how LanteaCraft is used. You won't be asked again until you upgrade or delete your configuration files.",
						20, 20, 390, 0xffffff);
		String[] segments = label.split("&");
		for (int i = 0; i < segments.length; i++) {
			String chunk = segments[i].trim();
			if (chunk.length() > 0) {
				String name = chunk.substring(0, chunk.indexOf('='));
				String value = chunk.substring(chunk.indexOf('=') + 1);
				drawString(this.fontRenderer, name, 20, 65 + (i * 15), 0xCDFFFF);
				drawString(this.fontRenderer, value, 120, 65 + (i * 15), 0xCDFFFF);
			}
		}
		drawCenteredString(this.fontRenderer, "More information at http://lanteacraft.com/stats", this.width / 2,
				this.height / 4 + 132, 0xA0A0A0);

		super.drawScreen(par1, par2, par3);
	}
}
