//Class by Nathan Purwosumarto - 11/17/15

package ta_bot;
//Imports
import robocode.*;

//Class for retrieving and storing enemy data when scanned
public class TrackedEnemy{
	//Variable setup
	double enemyBearing; 
	double enemyDistance;
	double enemyEnergy;
	double enemyHeading;
	double enemyVelocity;
	double currentX;
	double currentY;
	double futureX;
	double futureY;
	
	//When an enemy is scanned, takes the data from the scanned robot event and stores it
	public void updateEnemy(ScannedRobotEvent e, Robot myRobot){
		enemyBearing = e.getBearing();
		enemyDistance = e.getDistance();
		enemyEnergy = e.getEnergy();
		enemyHeading = e.getHeading();
		enemyVelocity = e.getVelocity();
		//Find angle to calculate enemy position
		double absBearingDeg = (myRobot.getHeading() + e.getBearing());
		//Use absolute bearings instead of relative bearing
		if (absBearingDeg < 0) absBearingDeg += 360;
		//Calculates the x and y position of robot using trigonometry
		currentX = myRobot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();
		currentY = myRobot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();
	}
	
	//Set all enemy variables to 0
	public void reset(){
		enemyBearing = 0;
		enemyDistance = 0;
		enemyEnergy = 0;
		enemyHeading = 0;
		enemyVelocity = 0;
		currentX = 0;
		currentY = 0;
	}
	//return enemy bearing to the main robot
	public double getBearing(){
		return enemyBearing;
	}
	//return enemy distance distance to the main robot
	public double getDistance(){
		return enemyDistance;
	}
	//return enemy energy to the main robot
	public double getEnergy(){
		return enemyEnergy;
	}
	//return enemy heading to the main robot
	public double getHeading(){
		return enemyHeading;
	}
	//return enemy velocity to the main robot
	public double getVelocity(){
		return enemyVelocity;
	}
	//return enemy x position to the main robot
	public double getX(){
		return currentX;
	}
	//return enemy y position to the main robot
	public double getY(){
		return currentY;
	}
	//predict the future x position of enemy and return it to main robot
	public double getFutureX(double time){
		futureX = currentX + Math.sin(Math.toRadians(enemyHeading)) * enemyVelocity * time;
		return futureX;
	}
	//predict the future y position of enemy and return it to main robot
	public double getFutureY(double time){
		futureY = currentY + Math.cos(Math.toRadians(enemyHeading)) * enemyVelocity * time;
		return futureY;
	}
}


