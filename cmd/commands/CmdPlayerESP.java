package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.render.PlayerESP;

public class CmdPlayerESP extends Command {

	public CmdPlayerESP() {
		this.command = "playeresp";
		this.description = "Allows the player to see other players through ESP";
	}

	@Override
	public void command(String[] parameters) {
		PlayerESP module = (PlayerESP) Aoba.getInstance().mm.playeresp;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("PlayerESP toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("PlayerESP toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba playeresp [toggle] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba playeresp [toggle] [value]");
		}
	}
}