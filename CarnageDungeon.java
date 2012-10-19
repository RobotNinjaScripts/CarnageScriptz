import java.awt.Graphics;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

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
	
	private static int dungeonEntry = (48496);
	private static int ringOfKinship = (15707);
	
	final WidgetChild floorNumber = Widgets.get(947, 765);
	final WidgetChild confirmFloor = Widgets.get(947, 766);
	final WidgetChild confirmComplex = Widgets.get(938, 39);
	final WidgetChild changeComplex = Widgets.get(939, 102);
	final WidgetChild changeFloor = Widgets.get(939, 108);
	final WidgetChild complex1 = Widgets.get(938, 60);
	final WidgetChild complex2 = Widgets.get(938, 61);
	final WidgetChild complex3 = Widgets.get(938, 66);
	final WidgetChild complex4 = Widgets.get(938, 71);
	final WidgetChild complex5 = Widgets.get(938, 76);
	final WidgetChild complex6 = Widgets.get(938, 81);
	
	@Override
	public int loop() {
		
		return Random.nextInt(10, 50);
	}
}