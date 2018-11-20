//Blitz - A Robot made by Nathan Purwosumarto
// Java Period 4 - 11/17/2015

package ntbots;
//Imports 
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html
public class Blitz extends AdvancedRobot {
	//Variables
	int gunTurning = 360;
	int moveDirection = 1;
	int radarTurnDirection = 1;
	int radarTurnCounter = 0;
	int bulletHitCounter = 0;
	int bulletMissCounter = 0;
	boolean linearFire = true;
	boolean currentScan = false;
	//Set up enemy that we will track
	String currentTarget;
	TrackedEnemy enemy = new TrackedEnemy();

	// Robot's default behavior
	public void run() {
		// Initialization of the robot
		// Color setup
		setBodyColor(new Color(15, 0, 0));
		setGunColor(new Color(0, 50, 100));
		setRadarColor(new Color(0, 150, 255));
		setBulletColor(Color.black);
		setScanColor(Color.cyan);
		// Turn robot, gun, and radar independently
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);

		// Target that robot will track
		currentTarget = null;

		// Robot main loop
		while (true) {
			scanEnemy();
			circleEnemy();
			shootEnemy();
			execute();
		}
	}

	// When a robot is scanned by the radar
	public void onScannedRobot(ScannedRobotEvent e) {
		// If robot has no target, or the robot scanned is closer than previous
		// target
		if (currentTarget == null || e.getDistance() < enemy.getDistance() - 50) {
			currentTarget = e.getName();
			enemy.updateEnemy(e, this);
			bulletHitCounter = 0;
			bulletMissCounter = 0;
		}
		// If robot finds the target; updates enemy data
		if (currentTarget == e.getName()) {
			enemy.updateEnemy(e, this);
			// Found the tracked enemy, so start oscillating radar
			radarTurnCounter = 0;
			currentScan = true;
		}
	}

	//Turns gun to face the robot hit and fires hard
	public void onHitRobot(HitRobotEvent e) {
		moveDirection *= -1;
		if (e.getBearing() < (getGunHeading() - getHeading()) + 10
		|| (e.getBearing() < (getGunHeading() - getHeading()) - 10)) {
			setTurnGunRight(e.getBearing() - (getGunHeading() - getHeading()));
			setFire(3);
		}
	}
	
	//If bullet hits the target, reset bullet miss counter
	public void onBulletHit(BulletHitEvent event) {
		if (event.getName() == currentTarget) {
			bulletHitCounter++;
		}
	}
	//If bullet misses, add one to bullet counter
	public void onBulletMissed(BulletMissedEvent event) {
		bulletMissCounter++;
		//If 5 bullets miss in a row with current targeting, switch targeting method
		if(bulletHitCounter/bulletHitCounter < 0.2){
			linearFire = !linearFire;
			bulletHitCounter = 0;
			bulletHitCounter = 0;
		}
	}
	
	//If robot hits a wall, change direction
	public void onHitWall(HitWallEvent e){
		moveDirection *= -1;
	}

	public void onRobotDeath(RobotDeathEvent e) {
		//Check if the robot we were tracking died
		if (e.getName().equals(currentTarget)) {
			currentTarget = null;
			enemy.reset();
			linearFire = true;
			bulletHitCounter = 0;
			bulletMissCounter = 0;
		}
	}

	//If robot was hit by a bullet
	public void onHitByBullet(HitByBulletEvent e) {
		// Turns tank perpendicular and points gun at direction bullet was fired from
		setTurnGunRight(normalRelativeAngleDegrees(e.getBearing() + getHeading() - getGunHeading()));
		setTurnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));
		setAhead(50);
		execute();
	}

	//Gets the absolute bearing of the angle between two points, used for linear targeting
	public double absoluteBearing(double x1, double y1, double x2, double y2) {
		double bearing = 0;
		double xshift = x2 - x1;
		double yshift = y2 - y1;
		// Calculates the length between the two points (distance formula)
		double hyplength = Math.pow((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)), 0.5);
		// Uses arcsine to find angle between robot and predicted enemy coordinates
		double calcAngle = Math.toDegrees(Math.asin(xshift / hyplength));
		if (xshift > 0 && yshift > 0) { // target in upper right
			bearing = calcAngle;
		} else if (xshift < 0 && yshift > 0) { // target in upper left
			bearing = 360 + calcAngle;
		} else if (xshift > 0 && yshift < 0) { // target in lower right
			bearing = 180 - calcAngle;
		} else if (xshift < 0 && yshift < 0) { // target in lower left
			bearing = 180 - calcAngle;
		}
		return bearing;
	}

	//Scan for the enemy
	public void scanEnemy() {
		if (currentTarget == null || radarTurnCounter > 2) {
			//Spin gun around in a circle if no enemy has been found
			setTurnRadarRight(360);
		} else {
			//If we have an enemy, swing gun back and forth 30 degrees to find it
			double moveRadarAngle = getHeading() - getRadarHeading() + enemy.getBearing();
			moveRadarAngle += 30 * radarTurnDirection;
			setTurnRadarRight(normalRelativeAngleDegrees(moveRadarAngle));
			radarTurnDirection *= -1;
			radarTurnCounter++;
		}
	}
	
	//Strafe back and forth, while slowly converging onto the enemy
	public void circleEnemy() {
		//Change directions every thirty ticks
		if(getTime() % 30 == 0){
			moveDirection *= -1;
		}
		if(enemy.getDistance() > 200){
		//Turn slightly into the enemy's trajectory if it is a fair distance away
		setTurnRight(normalRelativeAngleDegrees(enemy.getBearing() + 90 - (15 * moveDirection)));
		}
		//If enemy is already close, just turn perpendicular
		else{
		setTurnRight(normalRelativeAngleDegrees(enemy.getBearing() + 90));
		}
		//Move to circle around enemy
		setAhead(100 * moveDirection);
	}

	//Firing method - shoots the enemy
	public void shootEnemy(){
		//If enemy distance is very close, force Blitz to use head-on targeting
		if(enemy.getDistance() < 75){
			linearFire = false;
		}
		//If we have no target, return immediately, nothing to shoot at
		if(currentTarget == null){
			return;
		}
		//Shoots using linear targeting
		else if(linearFire == true){
			//determines firepower, bulletspeed, and time depending on enemy distance
			double firePower = 500 / enemy.getDistance();
			double bulletSpeed = 20 - firePower * 3;
			double time = (double)(enemy.getDistance() / bulletSpeed);
			//gets the absolute bearing with predicted x and y
			double predictedBearing = absoluteBearing(getX(), getY(), enemy.getFutureX(time), enemy.getFutureY(time));
			//turn gun to point at predicted location
			setTurnGunRight(normalRelativeAngleDegrees(predictedBearing - getGunHeading()));
			//if the gun can shoot and pointed correctly, shoot
			if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 5 && currentScan == true) {
				setFire(firePower);
				currentScan = false;
			}
		}
		//Shoot using head-on targeting
		else if(linearFire == false){
			//set firepower depending on distance
			double firePower = 500 / enemy.getDistance();
			//points the gun at current position of enemy
			double absoluteBearing = getHeading() + enemy.getBearing();
			setTurnGunRight(normalRelativeAngleDegrees(absoluteBearing - getGunHeading()));
			//if the gun can shoot and is pointed correctly, shoot
			if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 5 && currentScan == true) {
				setFire(firePower);
				currentScan = false;
			}
		}
	}
}
