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

package net.aoba.gui.components;

import com.mojang.logging.LogUtils;
import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.utils.render.Render2D;
import net.aoba.settings.types.StringSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class ListComponent extends Component implements MouseClickListener {
    private StringSetting listSetting;

    private List<String> options;
    private int selectedIndex;

    public ListComponent(IGuiElement parent, List<String> options) {
        super(parent, new Rectangle(null, null, null, 30f));
        this.setMargin(new Margin(2f, null, 2f, null));
        this.options = options;
    }

    public ListComponent(IGuiElement parent, List<String> options, StringSetting listSetting) {
        super(parent, new Rectangle(null, null, null, 30f));
        this.listSetting = listSetting;
        this.setMargin(new Margin(2f, null, 2f, null));
        this.options = options;
    }

    @Override
	public void onVisibilityChanged() {
    	super.onVisibilityChanged();
		if(this.isVisible())
			Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
		else
			Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
	}
    
    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        float stringWidth = Aoba.getInstance().fontManager.GetRenderer().getWidth(listSetting.getValue());
        
        float actualX = this.getActualSize().getX();
        float actualY = this.getActualSize().getY();
        float actualWidth = this.getActualSize().getWidth();
        
        Render2D.drawString(drawContext, listSetting.getValue(), actualX + (actualWidth / 2.0f) - stringWidth, actualY + 8, 0xFFFFFF);
        Render2D.drawString(drawContext, "<<", actualX + 8, actualY + 4, GuiManager.foregroundColor.getValue());
        Render2D.drawString(drawContext, ">>", actualX + 8 + (actualWidth - 34), actualY + 4, GuiManager.foregroundColor.getValue());
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
        listSetting.setValue(options.get(selectedIndex));
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
    	super.onMouseClick(event);

        if (event.button == MouseButton.LEFT) {
            if (this.getActualSize().getY() < event.mouseY && event.mouseY < this.getActualSize().getY() + this.getActualSize().getHeight()) {

                float mouseX = (float)event.mouseX;
                float actualX = this.getActualSize().getX();
                float actualWidth = this.getActualSize().getWidth();
                
                if (mouseX > actualX && mouseX < (actualX + 32)) {
                    setSelectedIndex(Math.max(selectedIndex - 1, 0));
                } else if (mouseX > (actualX + actualWidth - 32) && mouseX < (actualX + actualWidth))
                    setSelectedIndex(Math.min(selectedIndex + 1, options.size() - 1));
                
                event.cancel();
            }
        }
    }
}
