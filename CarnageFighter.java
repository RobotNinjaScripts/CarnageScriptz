import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Context;

@Manifest (authors = { "xCarnag3x" }, name = "CarnageFighter", description = "All NPCs and Food supported.", website = "http://www.powerbot.org/community/forum/10-combat/", version = 0.1, vip = false)
public class CarnageFighter extends ActiveScript implements PaintListener{

	private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
    }

    private final Color color1 = new Color(255, 255, 255);
    private final Color color2 = new Color(255, 0, 0);

    private final Font font1 = new Font("Arial", 0, 14);

    private final Image img1 = getImage("http://memberfiles.freewebs.com/76/94/94209476/photos/CarnageFighter/DSEuL.png");

    public void onRepaint(Graphics g1) {
        Graphics2D g = (Graphics2D)g1;
        strLvl = Skills.getRealLevel(Skills.STRENGTH);
        defLvl = Skills.getRealLevel(Skills.DEFENSE);
        atkLvl = Skills.getRealLevel(Skills.ATTACK);
        HPLvl = Skills.getRealLevel(Skills.CONSTITUTION);
        gainedStr = Skills.getRealLevel(Skills.STRENGTH) -startStr;
        gainedAtk = Skills.getRealLevel(Skills.ATTACK) -startAtk;
        gainedDef = Skills.getRealLevel(Skills.DEFENSE) -startDef;
        gainedHP = Skills.getRealLevel(Skills.CONSTITUTION) -startHP;
        totalGainedLevels = gainedStr + gainedAtk + gainedDef + gainedHP;
        g.setFont(font1);
        g.setColor(color1);
        g.drawImage(img1, -2, 389, null);
        g.drawString("  " +runTime.toElapsedString(), 149, 438);
        g.drawString("  " +monsterName, 173, 465);
        g.drawString("  " +attackStyle, 164, 491);
        g.drawString("  " +totalGainedLevels, 217, 515);
        g.drawString("  " +strLvl, 402, 437);
        g.drawString("  " +defLvl, 399, 464);
        g.drawString("  " +atkLvl, 398, 489);
        g.drawString("  " +HPLvl, 386, 516);
        g.setColor(color2);
        g.drawLine(Mouse.getLocation().x - 3000, Mouse.getLocation().y,
        		Mouse.getLocation().x + 3000, Mouse.getLocation().y);
        		g.drawLine(Mouse.getLocation

        		().x, Mouse.getLocation().y - 3000,
        						

        		Mouse.getLocation().x, Mouse.getLocation().y + 3000);
    }
	
    private final Timer runTime = new Timer(0);
    
    private final List<Node> jobsCollection = Collections.synchronizedList(new ArrayList<Node>());
	private Tree jobContainer = null;

	public final void provide(final Node... jobs) {
		for (final Node job : jobs) {
			jobsCollection.add(job);
		}
		jobContainer = new Tree(jobsCollection.toArray(new Node[jobsCollection.size()]));
	}
    
    private boolean combat = false;
    private boolean check = false;
    private boolean antiban = false;
    private boolean eat = false;
    private boolean prayers = false;
    private boolean protectMeele = false;
    private boolean protectRanged = false;
    private boolean protectMagic = false;
    
    private int strLvl = 0;
    private int defLvl = 0;
    private int atkLvl = 0;
    private int HPLvl = 0;
    private int gainedStr = 0;
    private int gainedAtk = 0;
    private int gainedDef = 0;
    private int gainedHP = 0;
    private int totalGainedLevels = 0;
    private int eatPercent = 0;
    private int foodId;
    private int startStr;
    private int startAtk;
    private int startDef;
    private int startHP;
    private int whichAntiban = 0;
    
    private String monsterName = "Not set yet...";
    private String attackStyle = "Not set yet...";
    
    @Override
    public void onStart(){
    	if(!Game.isLoggedIn()){
    		log.severe("You are not logged in!! Please log in and then start the script. :]");
    		stop();
    	}
    	Task.sleep(10);
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                    new CarnageFighterOption().setVisible(true);
            }
    });
    	startStr = Skills.getRealLevel(Skills.STRENGTH);
    	startAtk = Skills.getRealLevel(Skills.ATTACK);
    	startDef = Skills.getRealLevel(Skills.DEFENSE);
    	startHP = Skills.getRealLevel(Skills.CONSTITUTION);
    	provide(new Attack(), new Eat(), new Prayers());
    }
    
    private class AntiBan extends Node{

		@Override
		public boolean activate() {
			return antiban;
		}

		@Override
		public void execute() {
			if(whichAntiban == 0){
				Camera.setAngle(50);
				whichAntiban = 1;
			}else if(whichAntiban == 1){
				Tabs.FRIENDS.open();
				Task.sleep(5000, 6000);
				Tabs.INVENTORY.open();
				whichAntiban = 0;
			}
		}
    }
    
    private class Attack extends Node{
    	@Override
		public boolean activate() {
    		return combat;
		}
		
		@Override
		public void execute() {
			if(attackStyle.equals("Strength (Aggressive)")){
	    		final WidgetChild strengthClick = Widgets.get(884, 8);
	    		Tabs.ATTACK.open();
	    		if(strengthClick.validate()){
	    			strengthClick.click(true);
	    			Tabs.INVENTORY.open();
	    		}
	    	}else if(attackStyle.equals("Defensive (Block)")){
	    		final WidgetChild defenseClick = Widgets.get(884, 10);
	    		Tabs.ATTACK.open();
	    		if(defenseClick.validate()){
	    			defenseClick.click(true);
	    			Tabs.INVENTORY.open();
	    		}
	    		Tabs.INVENTORY.open();
	    	}else if(attackStyle.equals("Attack  (Accurate)")){
	 	  		final WidgetChild attackClick = Widgets.get(884, 7);
	 	  		Tabs.ATTACK.open();
	 	  		if(attackClick.validate()){
	    			attackClick.click(true);
	    			Tabs.INVENTORY.open();
	    		}
	 	  		Tabs.INVENTORY.open();
	    	}
			HPLvl = 999;
			if(Inventory.getCount(foodId) == 0){
				log.severe("[CarnageFighter] Out of Food, the script will now stop.");
				stop();
			}
			if (!Players.getLocal().isInCombat()){
				if (Players.getLocal().getHpPercent() < eatPercent){
					final Item food = Inventory.getItem(foodId);
					food.getWidgetChild().interact("Eat");
				}else if (Players.getLocal().isInCombat()){
					if (Players.getLocal().getHpPercent() < eatPercent){
						final Item food = Inventory.getItem(foodId);
						food.getWidgetChild().interact("Eat");
					}
				}
				NPC monster = NPCs.getNearest(monsterName);
				if(monster.isOnScreen()){
					monster.interact("Attack");
					Task.sleep(5000);
					if(monster.isInCombat()){
						Task.sleep(100);
					}
				}else if(!monster.isOnScreen()){
					Camera.setPitch(75);
					Task.sleep(50);
					if(monster.isOnScreen()){
						monster.interact("Attack");
						Task.sleep(5000);
						if(monster.isInCombat()){
							Task.sleep(100);
						}
					}
				}
			}
		}
    }
    
    private class Eat extends Node{

		@Override
		public boolean activate() {
			return eat;
		}

		@Override
		public void execute() {
			totalGainedLevels = 99999;
			if (Players.getLocal().getHpPercent() < eatPercent){
				final Item food = Inventory.getItem(foodId);
				food.getWidgetChild().interact("Eat");
			}
			if (check){
				if (Inventory.getCount(foodId) == 0){
					Game.logout(true);
				}
			}
			
		}
    }
    
    private class Prayers extends Node{

		@Override
		public boolean activate() {
			return prayers;
		}

		@Override
		public void execute() {
			totalGainedLevels = 111111;
			if (Players.getLocal().isInCombat()){
				if(prayers){
					if(protectMeele){
						Tabs.PRAYER.open();
						final WidgetChild meeleClick = Widgets.get(7, 19);
						meeleClick.click(true);
					}
					if(protectRanged){
						Tabs.PRAYER.open();
						final WidgetChild rangedClick = Widgets.get(7, 18);
						rangedClick.click(true);
					}
					if(protectMagic){
						Tabs.PRAYER.open();
						final WidgetChild magicClick = Widgets.get(7, 19);
						magicClick.click(true);
					}
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
	
	/*
	 * To change this template, choose Tools | Templates
	 * and open the template in the editor.
	 */
	public class CarnageFighterOption extends javax.swing.JFrame {

	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
	     * Creates new form CarnageFighterOption
	     */
	    public CarnageFighterOption() {
	        initComponents();
	    }

	    /**
	     * This method is called from within the constructor to initialize the form.
	     * WARNING: Do NOT modify this code. The content of this method is always
	     * regenerated by the Form Editor.
	     */
	    
	    private void initComponents() {

	        jDialog1 = new javax.swing.JDialog();
	        jLabel3 = new javax.swing.JLabel();
	        jLabel4 = new javax.swing.JLabel();
	        jLabel5 = new javax.swing.JLabel();
	        jLabel6 = new javax.swing.JLabel();
	        jLabel7 = new javax.swing.JLabel();
	        jLabel8 = new javax.swing.JLabel();
	        jLabel9 = new javax.swing.JLabel();
	        jLabel1 = new javax.swing.JLabel();
	        jLabel2 = new javax.swing.JLabel();
	        jButton1 = new javax.swing.JButton();
	        jButton2 = new javax.swing.JButton();
	        jLabel10 = new javax.swing.JLabel();
	        jLabel11 = new javax.swing.JLabel();
	        jTextField1 = new javax.swing.JTextField();
	        jTextField2 = new javax.swing.JTextField();
	        jSlider1 = new javax.swing.JSlider();
	        jLabel12 = new javax.swing.JLabel();
	        jPanel1 = new javax.swing.JPanel();
	        jCheckBox1 = new javax.swing.JCheckBox();
	        jRadioButton1 = new javax.swing.JRadioButton();
	        jRadioButton2 = new javax.swing.JRadioButton();
	        jRadioButton3 = new javax.swing.JRadioButton();
	        jRadioButton4 = new javax.swing.JRadioButton();
	        jRadioButton5 = new javax.swing.JRadioButton();
	        jRadioButton6 = new javax.swing.JRadioButton();
	        jLabel13 = new javax.swing.JLabel();

	        jDialog1.setVisible(false);

	        jLabel3.setText("Hello there, you're probably here because you do not know the ID of your food type. Find out your self by:");

	        jLabel4.setText("1. Open RSBot in DEV mode.");

	        jLabel5.setText("2. Log into Runescape.");

	        jLabel6.setText("3. Have your food ready in your Inventory.");

	        jLabel7.setText("4. Press the Settings icon, top left in RSBot.");

	        jLabel8.setText("5. Open View --> Inventory.");

	        jLabel9.setText("6. The green number above your food is the ID.");

	        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
	        jDialog1.getContentPane().setLayout(jDialog1Layout);
	        jDialog1Layout.setHorizontalGroup(
	            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jDialog1Layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(jLabel3)
	                    .addComponent(jLabel4)
	                    .addComponent(jLabel5)
	                    .addComponent(jLabel6)
	                    .addComponent(jLabel7)
	                    .addComponent(jLabel8)
	                    .addComponent(jLabel9))
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
	        jDialog1Layout.setVerticalGroup(
	            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jDialog1Layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jLabel3)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(jLabel4)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(jLabel5)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(jLabel6)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(jLabel7)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(jLabel8)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(jLabel9)
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );

	        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

	        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
	        jLabel1.setText("Welcome To CarnageFighter v0.1 Alpha");

	        jLabel2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
	        jLabel2.setText("Settings:");

	        jButton1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
	        jButton1.setText("Start Fighting!");
	        jButton1.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jButton1ActionPerformed(evt);
	            }
	        });

	        jButton2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
	        jButton2.setText("How to get food IDs");
	        jButton2.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jButton2ActionPerformed(evt);
	            }
	        });

	        jLabel10.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
	        jLabel10.setText("Monster Name:");

	        jLabel11.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
	        jLabel11.setText("Food ID:");

	        jTextField1.setText("Monster Name goes here :)");
	        jTextField1.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jTextField1ActionPerformed(evt);
	            }
	        });

	        jTextField2.setText("Food id goes here :)");
	        jTextField2.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jTextField2ActionPerformed(evt);
	            }
	        });

	        jSlider1.setMajorTickSpacing(20);
	        jSlider1.setMinorTickSpacing(5);
	        jSlider1.setPaintLabels(true);
	        jSlider1.setPaintTicks(true);
	        jSlider1.setValue(80);

	        jLabel12.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
	        jLabel12.setText("Eat Percent:");

	        jCheckBox1.setText("Use Prayers");
	        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jCheckBox1ActionPerformed(evt);
	            }
	        });

	        jRadioButton1.setText("Protect from Melee");
	        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jRadioButton1ActionPerformed(evt);
	            }
	        });

	        jRadioButton2.setText("Protect from Ranged");
	        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jRadioButton2ActionPerformed(evt);
	            }
	        });

	        jRadioButton3.setText("Protect from Magic");
	        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jRadioButton3ActionPerformed(evt);
	            }
	        });

	        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	        jPanel1.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(jCheckBox1)
	                    .addComponent(jRadioButton1)
	                    .addComponent(jRadioButton2)
	                    .addComponent(jRadioButton3))
	                .addGap(0, 93, Short.MAX_VALUE))
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addComponent(jCheckBox1)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(jRadioButton1)
	                .addGap(18, 18, 18)
	                .addComponent(jRadioButton2)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(jRadioButton3)
	                .addContainerGap())
	        );

	        jRadioButton4.setText("Strength (Aggressive)");
	        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jRadioButton4ActionPerformed(evt);
	            }
	        });

	        jRadioButton5.setText("Attack (Accurate)");
	        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jRadioButton5ActionPerformed(evt);
	            }
	        });

	        jRadioButton6.setText("Defense (Block)");
	        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jRadioButton6ActionPerformed(evt);
	            }
	        });

	        jLabel13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
	        jLabel13.setText("Attack Style:");

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
	                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	                            .addContainerGap()
	                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                        .addGroup(layout.createSequentialGroup()
	                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                .addGroup(layout.createSequentialGroup()
	                                    .addContainerGap()
	                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
	                                        .addComponent(jSlider1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	                                            .addComponent(jLabel10)
	                                            .addGap(18, 18, 18)
	                                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
	                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	                                            .addComponent(jLabel11)
	                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                                            .addComponent(jTextField2))))
	                                .addGroup(layout.createSequentialGroup()
	                                    .addGap(104, 104, 104)
	                                    .addComponent(jLabel12)))
	                            .addGap(18, 18, 18)
	                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                            .addGap(18, 18, 18)
	                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                .addComponent(jRadioButton4)
	                                .addComponent(jRadioButton5)
	                                .addComponent(jRadioButton6)
	                                .addComponent(jLabel13))))
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(108, 108, 108)
	                        .addComponent(jLabel1)))
	                .addContainerGap())
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                .addGap(0, 0, Short.MAX_VALUE)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                        .addComponent(jButton2)
	                        .addGap(248, 248, 248))
	                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                        .addComponent(jLabel2)
	                        .addGap(283, 283, 283))))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jLabel1)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jLabel2)
	                .addGap(17, 17, 17)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addGroup(layout.createSequentialGroup()
	                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                                    .addComponent(jLabel10)
	                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                                .addGap(18, 18, 18)
	                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                                    .addComponent(jLabel11)
	                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                                .addGap(11, 11, 11)
	                                .addComponent(jLabel12)
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                        .addGap(14, 14, 14)
	                        .addComponent(jButton2))
	                    .addGroup(layout.createSequentialGroup()
	                        .addComponent(jLabel13)
	                        .addGap(12, 12, 12)
	                        .addComponent(jRadioButton4)
	                        .addGap(18, 18, 18)
	                        .addComponent(jRadioButton5)
	                        .addGap(18, 18, 18)
	                        .addComponent(jRadioButton6)
	                        .addGap(0, 0, Short.MAX_VALUE)))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jButton1)
	                .addContainerGap())
	        );

	        pack();
	    }// </editor-fold>

	    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
	        jDialog1.setVisible(true);
	    }

	    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {
	    	if(jRadioButton5.isSelected()){
	            jRadioButton5.setSelected(false);
	        }
	        if(jRadioButton6.isSelected()){
	            jRadioButton6.setSelected(false);
	        }
	    }

	    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {
	    	if(jRadioButton4.isSelected()){
	            jRadioButton4.setSelected(false);
	        }
	        if(jRadioButton6.isSelected()){
	            jRadioButton6.setSelected(false);
	        }
	    }

	    private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {
	    	if(jRadioButton4.isSelected()){
	            jRadioButton4.setSelected(false);
	        }
	        if(jRadioButton5.isSelected()){
	            jRadioButton5.setSelected(false);
	        }
	    }

	    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
	        if(jRadioButton4.isSelected()){
	        	attackStyle = "Strength (Aggressive)";
	        }else if (jRadioButton5.isSelected()){
	        	attackStyle = "Defensive (Block)";
	        }else if (jRadioButton6.isSelected()){
	        	attackStyle = "Attack  (Accurate)";
	        }
	        monsterName = jTextField1.getText();
	        prayers = jCheckBox1.isSelected();
	        protectMeele = jRadioButton1.isSelected();
	        protectRanged = jRadioButton2.isSelected();
	        protectMagic = jRadioButton3.isSelected();
	        eatPercent = jSlider1.getValue();
	        combat = true;
	        check = true;
	        antiban = true;
	        eat = true;
	        dispose();
	    }

	    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {
	    	if(jRadioButton2.isSelected()){
	            jRadioButton2.setSelected(false);
	        }
	        if(jRadioButton3.isSelected()){
	            jRadioButton3.setSelected(false);
	        }
	    }

	    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {
	    	if(jRadioButton1.isSelected()){
	            jRadioButton1.setSelected(false);
	        }
	        if(jRadioButton3.isSelected()){
	            jRadioButton3.setSelected(false);
	        }
	    }

	    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {
	    	if(jRadioButton1.isSelected()){
	            jRadioButton1.setSelected(false);
	        }
	        if(jRadioButton2.isSelected()){
	            jRadioButton2.setSelected(false);
	        }
	    }

	    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {
	        // TODO add your handling code here:
	    }

	    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {
	    	foodId = Integer.parseInt(jTextField2.getText());
	    }

	    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
	    	monsterName = jTextField1.getText();
	    }

	    /**
	     * @param args the command line arguments
	     */
	    
	    // Variables declaration - do not modify
	    private javax.swing.JButton jButton1;
	    private javax.swing.JButton jButton2;
	    private javax.swing.JCheckBox jCheckBox1;
	    private javax.swing.JDialog jDialog1;
	    private javax.swing.JLabel jLabel1;
	    private javax.swing.JLabel jLabel10;
	    private javax.swing.JLabel jLabel11;
	    private javax.swing.JLabel jLabel12;
	    private javax.swing.JLabel jLabel13;
	    private javax.swing.JLabel jLabel2;
	    private javax.swing.JLabel jLabel3;
	    private javax.swing.JLabel jLabel4;
	    private javax.swing.JLabel jLabel5;
	    private javax.swing.JLabel jLabel6;
	    private javax.swing.JLabel jLabel7;
	    private javax.swing.JLabel jLabel8;
	    private javax.swing.JLabel jLabel9;
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JRadioButton jRadioButton1;
	    private javax.swing.JRadioButton jRadioButton2;
	    private javax.swing.JRadioButton jRadioButton3;
	    private javax.swing.JRadioButton jRadioButton4;
	    private javax.swing.JRadioButton jRadioButton5;
	    private javax.swing.JRadioButton jRadioButton6;
	    private javax.swing.JSlider jSlider1;
	    private javax.swing.JTextField jTextField1;
	    private javax.swing.JTextField jTextField2;
	    // End of variables declaration
	}
}
