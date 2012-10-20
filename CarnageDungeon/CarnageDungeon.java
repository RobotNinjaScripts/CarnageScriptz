import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.*;

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
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CarnageDungeonGUI().setVisible(true);
			}
		});
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
	public boolean prayers;       // TODO booleans
	public boolean prestige;
	
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
	
	public class CarnageDungeonGUI extends JFrame {
		public CarnageDungeonGUI() {
			initComponents();
		}
		
		private void startButtonActionPerformed(ActionEvent e) {
			if(usePrayers.isSelected()){
				prayers = true;
			}
			if(prestigeBox.isSelected()){
				prestige = true;
			}
			if(floorSizeBox.equals("Small")){
				dungeonSize = 1;
			}else if(floorSizeBox.equals("Medium")){
				dungeonSize = 2;
			}else if(floorSizeBox.equals("Large")){
				dungeonSize = 3;
			}
			if(complexityBox.equals("1")){
				complexity = 1;
			}else if(complexityBox.equals("2")){
				complexity = 2;
			}else if(complexityBox.equals("3")){
				complexity = 3;
			}else if(complexityBox.equals("4")){
				complexity = 4;
			}else if(complexityBox.equals("5")){
				complexity = 5;
			}else if(complexityBox.equals("6")){
				complexity = 6;
			}
			dispose();
		}

		private void initComponents() {
			titlePanel = new JPanel();
			mainTitle = new JLabel();
			subTitle = new JLabel();
			mainPanel = new JPanel();
			leftPanel = new JPanel();
			usePrayers = new JCheckBox();
			combatStyle = new JCheckBox();
			upgradeArmour = new JCheckBox();
			upgradeWeapons = new JCheckBox();
			makeFood = new JCheckBox();           // TODO booleans
			exploreDungeon = new JCheckBox();
			startPanel = new JPanel();
			startButton = new JButton();
			developerMode = new JCheckBox();
			rightPanel = new JPanel();
			complexityText = new JLabel();
			complexityBox = new JComboBox<>();
			floorSizeText = new JLabel();
			floorSizeBox = new JComboBox<>();
			prestigeBox = new JCheckBox();

			//======== this ========
			setTitle("CarnageDungeon - Version 0.01");
			setResizable(false);
			Container contentPane = getContentPane();

			//======== titlePanel ========
			{
				titlePanel.setBorder(LineBorder.createBlackLineBorder());

				//---- mainTitle ----
				mainTitle.setText("CarnageDungeon");
				mainTitle.setFont(new Font("Arial", Font.BOLD, 30));
				mainTitle.setHorizontalAlignment(SwingConstants.CENTER);

				//---- subTitle ----
				subTitle.setText("By xCarnag3x and RobotNinja");
				subTitle.setFont(new Font("Arial", Font.PLAIN, 14));
				subTitle.setHorizontalAlignment(SwingConstants.CENTER);

				GroupLayout titlePanelLayout = new GroupLayout(titlePanel);
				titlePanel.setLayout(titlePanelLayout);
				titlePanelLayout.setHorizontalGroup(
					titlePanelLayout.createParallelGroup()
						.addComponent(subTitle, GroupLayout.PREFERRED_SIZE, 506, GroupLayout.PREFERRED_SIZE)
						.addComponent(mainTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				);
				titlePanelLayout.setVerticalGroup(
					titlePanelLayout.createParallelGroup()
						.addGroup(titlePanelLayout.createSequentialGroup()
							.addGap(10, 10, 10)
							.addComponent(mainTitle)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(subTitle)
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
			}

			//======== mainPanel ========
			{
				mainPanel.setBorder(LineBorder.createBlackLineBorder());

				//======== leftPanel ========
				{
					leftPanel.setBorder(LineBorder.createBlackLineBorder());

					//---- usePrayers ----
					usePrayers.setText("Use Prayers");
					usePrayers.setFont(usePrayers.getFont().deriveFont(usePrayers.getFont().getSize() + 1f));

					//---- combatStyle ----
					combatStyle.setText("Change Combat Style");
					combatStyle.setFont(combatStyle.getFont().deriveFont(combatStyle.getFont().getSize() + 1f));

					//---- upgradeArmour ----
					upgradeArmour.setText("Upgrade Armour");
					upgradeArmour.setFont(upgradeArmour.getFont().deriveFont(upgradeArmour.getFont().getSize() + 1f));

					//---- upgradeWeapons ----
					upgradeWeapons.setText("Upgrade Weapons");
					upgradeWeapons.setFont(upgradeWeapons.getFont().deriveFont(upgradeWeapons.getFont().getSize() + 1f));

					//---- makeFood ----
					makeFood.setText("Make Food");
					makeFood.setFont(makeFood.getFont().deriveFont(makeFood.getFont().getSize() + 1f));

					//---- exploreDungeon ----
					exploreDungeon.setText("Explore Dungeon");
					exploreDungeon.setFont(exploreDungeon.getFont().deriveFont(exploreDungeon.getFont().getSize() + 1f));

					GroupLayout leftPanelLayout = new GroupLayout(leftPanel);
					leftPanel.setLayout(leftPanelLayout);
					leftPanelLayout.setHorizontalGroup(
						leftPanelLayout.createParallelGroup()
							.addGroup(leftPanelLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(leftPanelLayout.createParallelGroup()
									.addComponent(usePrayers, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(combatStyle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(upgradeArmour, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(upgradeWeapons, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(makeFood, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(exploreDungeon, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addContainerGap())
					);
					leftPanelLayout.setVerticalGroup(
						leftPanelLayout.createParallelGroup()
							.addGroup(leftPanelLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(usePrayers, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(combatStyle, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(upgradeArmour, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(upgradeWeapons, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(makeFood, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(exploreDungeon, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(17, Short.MAX_VALUE))
					);
				}

				//======== startPanel ========
				{
					startPanel.setBorder(LineBorder.createBlackLineBorder());

					//---- startButton ----
					startButton.setText("Start");
					startButton.setFont(new Font("Arial", Font.BOLD, 20));

					//---- developerMode ----
					developerMode.setText("Developer Mode");
					developerMode.setFont(developerMode.getFont().deriveFont(developerMode.getFont().getSize() + 4f));

					GroupLayout startPanelLayout = new GroupLayout(startPanel);
					startPanel.setLayout(startPanelLayout);
					startPanelLayout.setHorizontalGroup(
						startPanelLayout.createParallelGroup()
							.addGroup(startPanelLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(startButton, GroupLayout.PREFERRED_SIZE, 206, GroupLayout.PREFERRED_SIZE)
								.addGap(29, 29, 29)
								.addComponent(developerMode)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
					startPanelLayout.setVerticalGroup(
						startPanelLayout.createParallelGroup()
							.addGroup(startPanelLayout.createSequentialGroup()
								.addGap(9, 9, 9)
								.addGroup(startPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(developerMode, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
									.addComponent(startButton, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				//======== rightPanel ========
				{
					rightPanel.setBorder(LineBorder.createBlackLineBorder());

					//---- complexityText ----
					complexityText.setText("Complexity");
					complexityText.setHorizontalAlignment(SwingConstants.CENTER);
					complexityText.setFont(complexityText.getFont().deriveFont(complexityText.getFont().getSize() + 10f));

					//---- complexityBox ----
					complexityBox.setModel(new DefaultComboBoxModel<>(new String[] {
						"1",
						"2",
						"3",
						"4",
						"5",
						"6"
					}));
					complexityBox.setFont(complexityBox.getFont().deriveFont(complexityBox.getFont().getStyle() | Font.BOLD, complexityBox.getFont().getSize() + 3f));

					//---- floorSizeText ----
					floorSizeText.setText("Floor Size");
					floorSizeText.setHorizontalAlignment(SwingConstants.CENTER);
					floorSizeText.setFont(floorSizeText.getFont().deriveFont(floorSizeText.getFont().getSize() + 10f));

					//---- floorSizeBox ----
					floorSizeBox.setModel(new DefaultComboBoxModel<>(new String[] {
						"Small",
						"Medium",
						"Large"
					}));
					floorSizeBox.setFont(floorSizeBox.getFont().deriveFont(floorSizeBox.getFont().getStyle() | Font.BOLD, floorSizeBox.getFont().getSize() + 3f));

					//---- prestigeBox ----
					prestigeBox.setText("   Prestige");
					prestigeBox.setFont(prestigeBox.getFont().deriveFont(prestigeBox.getFont().getSize() + 10f));

					GroupLayout rightPanelLayout = new GroupLayout(rightPanel);
					rightPanel.setLayout(rightPanelLayout);
					rightPanelLayout.setHorizontalGroup(
						rightPanelLayout.createParallelGroup()
							.addGroup(rightPanelLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(rightPanelLayout.createParallelGroup()
									.addComponent(complexityText, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
									.addComponent(complexityBox, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
									.addComponent(floorSizeText, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
									.addComponent(floorSizeBox, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
									.addComponent(prestigeBox, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
								.addContainerGap())
					);
					rightPanelLayout.setVerticalGroup(
						rightPanelLayout.createParallelGroup()
							.addGroup(rightPanelLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(complexityText, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(complexityBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(floorSizeText, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(floorSizeBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(prestigeBox, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
				mainPanel.setLayout(mainPanelLayout);
				mainPanelLayout.setHorizontalGroup(
					mainPanelLayout.createParallelGroup()
						.addGroup(mainPanelLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(mainPanelLayout.createParallelGroup()
								.addGroup(mainPanelLayout.createSequentialGroup()
									.addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addGap(18, 18, 18)
									.addComponent(rightPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(startPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addContainerGap())
				);
				mainPanelLayout.setVerticalGroup(
					mainPanelLayout.createParallelGroup()
						.addGroup(mainPanelLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(mainPanelLayout.createParallelGroup()
								.addComponent(leftPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(18, 18, 18)
							.addComponent(startPanel, GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
							.addContainerGap())
				);
			}

			GroupLayout contentPaneLayout = new GroupLayout(contentPane);
			contentPane.setLayout(contentPaneLayout);
			contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup()
					.addGroup(contentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
							.addComponent(titlePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap(22, Short.MAX_VALUE))
			);
			contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup()
					.addGroup(contentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(18, Short.MAX_VALUE))
			);
			setLocationRelativeTo(getOwner());
		}

		private JPanel titlePanel;
		private JLabel mainTitle;
		private JLabel subTitle;
		private JPanel mainPanel;
		private JPanel leftPanel;
		private JCheckBox usePrayers;
		private JCheckBox combatStyle;
		private JCheckBox upgradeArmour;
		private JCheckBox upgradeWeapons;
		private JCheckBox makeFood;
		private JCheckBox exploreDungeon;
		private JPanel startPanel;
		private JButton startButton;
		private JCheckBox developerMode;
		private JPanel rightPanel;
		private JLabel complexityText;
		private JComboBox<String> complexityBox;
		private JLabel floorSizeText;
		private JComboBox<String> floorSizeBox;
		private JCheckBox prestigeBox;
	}
}