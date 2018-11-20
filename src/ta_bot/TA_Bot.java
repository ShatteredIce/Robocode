//Blitz - A Robot made by Nathan Purwosumarto
// Java Period 4 - 11/17/2015

package ta_bot;
//Imports 
import robocode.*;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html
public class TA_Bot extends AdvancedRobot {
	//Variables
	
	//Set up enemies that we will track
	String[] currentEnemies = new String[7];
	TrackedEnemy[] enemyData = new TrackedEnemy[7];
	int numSavedEnemies = 0;
	DummyNet net = new DummyNet();

	
	// Robot's default behavior
	public void run() {
		// Initialization of the robot
		// Color setup
		setBodyColor(Color.black);
		setGunColor(new Color(15, 0, 0));
		setRadarColor(new Color(15, 0, 0));
		setBulletColor(Color.black);
		setScanColor(new Color(15,0,0));
		// Turn robot, gun, and radar independently
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		
		// Robot main loop
		while (true) {
			setTurnRadarRight(360);
		}
	}

	// When a robot is scanned by the radar
	public void onScannedRobot(ScannedRobotEvent e) {
		String currentScanned = e.getName();
		boolean createNewEntry = true;
		for(int i = 0; i < numSavedEnemies; i++) {
			if(currentEnemies[i].equals(currentScanned)) {
				enemyData[i].updateEnemy(e, this);
				createNewEntry = false;
			}
		}
		if(createNewEntry) {
			currentEnemies[numSavedEnemies] = e.getName();
			enemyData[numSavedEnemies].updateEnemy(e, this);
			numSavedEnemies++;
		}
	}

	public void onHitRobot(HitRobotEvent e) {

	}
	

	public void onRobotDeath(RobotDeathEvent e) {

	}

	//If robot was hit by a bullet
	public void onHitByBullet(HitByBulletEvent e) {

	}
	
	public void shootEnemy() {
		
	}
}
