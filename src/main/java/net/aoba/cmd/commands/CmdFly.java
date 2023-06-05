/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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

package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.movement.Fly;

public class CmdFly extends Command {

	public CmdFly() {
		this.description = "Allows the player to fly";
	}

	@Override
	public void command(String[] parameters) {
		for(int i = 0; i < parameters.length; i++) {
			System.out.println(parameters[i]);
		}
		Fly module = (Fly) Aoba.getInstance().mm.fly;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "speed":
				try {
					float speed = Float.parseFloat(parameters[1]);
					module.setSpeed(speed);
					CommandManager.sendChatMessage("Flight speed set to " + speed);

				} catch (Exception e) {
					CommandManager.sendChatMessage("Invalid value.");
				}
				break;
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Fly toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Fly toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba fly [speed, toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba fly [speed, toggle] [value]");
		}
	}
}