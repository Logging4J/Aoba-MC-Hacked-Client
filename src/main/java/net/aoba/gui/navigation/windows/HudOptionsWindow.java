/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.windows;

import java.util.Arrays;
import java.util.List;

import com.mojang.logging.LogUtils;

import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.components.CheckboxComponent;
import net.aoba.gui.components.ColorPickerComponent;
import net.aoba.gui.components.KeybindComponent;
import net.aoba.gui.components.ListComponent;
import net.aoba.gui.components.SliderComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.navigation.Window;
import net.minecraft.client.gui.DrawContext;

public class HudOptionsWindow extends Window {
	public HudOptionsWindow() {
		super("Hud Options", 600, 200);

		this.minWidth = 340.0f;

		StackPanelComponent stackPanel = new StackPanelComponent(this);
		stackPanel.setMargin(new Margin(null, 30f, null, null));

		List<String> fontNames = Aoba.getInstance().fontManager.fontRenderers.keySet().stream().toList();
		LogUtils.getLogger().info(Arrays.toString(fontNames.toArray()));

		// Keybinds Header
		stackPanel.addChild(new StringComponent(stackPanel, "Keybinds", GuiManager.foregroundColor.getValue(), true));

		stackPanel.addChild(new CheckboxComponent(stackPanel, GuiManager.enableCustomTitle));

		KeybindComponent clickGuiKeybindComponent = new KeybindComponent(stackPanel,
				Aoba.getInstance().guiManager.clickGuiButton);

		stackPanel.addChild(clickGuiKeybindComponent);

		// Hud Font Header
		stackPanel.addChild(new StringComponent(stackPanel, "HUD Font", GuiManager.foregroundColor.getValue(), true));

		ListComponent listComponent = new ListComponent(stackPanel, fontNames,
				Aoba.getInstance().fontManager.fontSetting);
		stackPanel.addChild(listComponent);

		stackPanel.addChild(new StringComponent(stackPanel, "HUD Colors", GuiManager.foregroundColor.getValue(), true));

		stackPanel.addChild(new ColorPickerComponent(stackPanel, GuiManager.foregroundColor));
		stackPanel.addChild(new ColorPickerComponent(stackPanel, GuiManager.backgroundColor));
		stackPanel.addChild(new ColorPickerComponent(stackPanel, GuiManager.borderColor));

		stackPanel
				.addChild(new StringComponent(stackPanel, "Hud Styling", GuiManager.foregroundColor.getValue(), true));

		stackPanel.addChild(new SliderComponent(stackPanel, GuiManager.roundingRadius));

		stackPanel.addChild(new StringComponent(stackPanel, "GUI / HUD Responsiveness",
				GuiManager.foregroundColor.getValue(), true));

		stackPanel.addChild(new SliderComponent(stackPanel, GuiManager.dragSmoothening));

		this.children.add(stackPanel);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
	}
}
