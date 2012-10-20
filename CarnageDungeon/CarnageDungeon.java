import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.*;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@Manifest (authors = { "xCarnag3x, RobotNinja" }, name = "CarnageDungeon", description = "The Best Dungeoneering bot. Ever.", website = "", version = 0.1, vip = false)
public class CarnageDungeon extends ActiveScript implements PaintListener{

	private final Color color2 = new Color(255, 0, 0);
	
	@Override
	public void onRepaint(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		g.setColor(color2);
        g.drawLine(Mouse.getLocation().x - 3000, Mouse.getLocation().y,
        		Mouse.getLocation().x + 3000, Mouse.getLocation().y);
        		g.drawLine(Mouse.getLocation

        		().x, Mouse.getLocation().y - 3000,
        						

        		Mouse.getLocation().x, Mouse.getLocation().y + 3000);
	}
	
	@Override
	public void onStart(){
		if(!Game.isLoggedIn()){
			log.severe("[CarnageDungeon] Please log in before starting the script! Thanks :]");
			stop();
		}
		Task.sleep(10);
		dungeoneeringLevel = Skills.getRealLevel(Skills.DUNGEONEERING);
		provide(new EnterDungeon());
	}
	
	private final List<Node> jobsCollection = Collections.synchronizedList(new ArrayList<Node>());
	private Tree jobContainer = null;

	public final void provide(final Node... jobs) {
		for (final Node job : jobs) {
			jobsCollection.add(job);
		}
		jobContainer = new Tree(jobsCollection.toArray(new Node[jobsCollection.size()]));
	}
	
	private static int dungeonEntry = (48496);
	private static int ringOfKinship = (15707);
	private int dungeoneeringLevel;
	private int complexity = 6;
	private int dungeonSize = 1;
	
	private boolean makeParty = true;
	private boolean enter = true;
	
	final WidgetChild floorNumber = Widgets.get(947, 765);
	final WidgetChild confirmFloor = Widgets.get(947, 766);
	final WidgetChild confirmComplex = Widgets.get(938, 39);
	final WidgetChild changeComplex = Widgets.get(939, 102);
	final WidgetChild changeFloor = Widgets.get(939, 108);
	final WidgetChild formParty = Widgets.get(939, 13);
	final WidgetChild smallDungeon = Widgets.get(1188, 3);
	final WidgetChild mediumDungeon = Widgets.get(1188, 24);
	final WidgetChild largeDungeon = Widgets.get(1188, 14);
	final WidgetChild complex1 = Widgets.get(938, 60);
	final WidgetChild complex2 = Widgets.get(938, 61);
	final WidgetChild complex3 = Widgets.get(938, 66);
	final WidgetChild complex4 = Widgets.get(938, 71);
	final WidgetChild complex5 = Widgets.get(938, 76);
	final WidgetChild complex6 = Widgets.get(938, 81);
	
	private class EnterDungeon extends Node{

		@Override
		public boolean activate() {
			return true;
		}

		@Override
		public void execute() {
			if(makeParty){
				final Item ringKinship = Inventory.getItem(ringOfKinship);
				ringKinship.getWidgetChild().interact("Open party interface");
				Task.sleep(3000);
				if(formParty.validate()){
					formParty.click(true);
				}
				Task.sleep(1500);
				makeParty = false;
			}
			if(enter){
				SceneObject entryToDungeon = SceneEntities.getNearest(dungeonEntry);
				entryToDungeon.interact("Climb-down");
				enter = false;
			}
			Task.sleep(5000);
			confirmFloor.click(true);
			if(complexity == 1){
				if(complex1.validate()){
					complex1.click(true);
				}
			}else if(complexity == 2){
				if(complex2.validate()){
					complex2.click(true);
				}
			}else if(complexity == 3){
				if(complex3.validate()){
					complex3.click(true);
				}
			}else if(complexity == 4){
				if(complex4.validate()){
					complex4.click(true);
				}
			}else if(complexity == 5){
				if(complex5.validate()){
					complex5.click(true);
				}
			}else if(complexity == 6){
				if(complex6.validate()){
					complex6.click(true);
				}
			}
			Task.sleep(500);
			confirmComplex.click(true);
			Task.sleep(1000);
			if(dungeonSize == 1){
				if(smallDungeon.validate()){
					smallDungeon.click(true);
				}
			}else if(dungeonSize == 2){
				if(mediumDungeon.validate()){
					mediumDungeon.click(true);
				}
			}else if(dungeonSize == 3){
				if(largeDungeon.validate()){
					largeDungeon.click(true);
				}
			}
		}
	}
	
	@Override
	public int loop() {
		if (jobContainer != null) {
			final Node job = jobContainer.state();
			if (job != null) {
				jobContainer.set(job);
				getContainer().submit(job);
				job.join();
			}
		}
		return Random.nextInt(10, 50);
	}
}