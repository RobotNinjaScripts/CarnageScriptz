import java.awt.Graphics;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.util.Random;

@Manifest (authors = { "xCarnag3x, RobotNinja" }, name = "CarnageDungeon", description = "The Best Dungeoneering bot. Ever.", website = "", version = 0.1, vip = false)
public class CarnageDungeon extends ActiveScript implements PaintListener{

	@Override
	public void onRepaint(Graphics arg0) {
		
	}
	
	@Override
	public void onStart(){
		if(!Game.isLoggedIn()){
			log.severe("[CarnageDungeon] Please log in before starting the script! Thanks :]");
			stop();
		}
	}
	
	// TODO script mains go here.
	
	@Override
	public int loop() {
		
		return Random.nextInt(10, 50);
	}
}