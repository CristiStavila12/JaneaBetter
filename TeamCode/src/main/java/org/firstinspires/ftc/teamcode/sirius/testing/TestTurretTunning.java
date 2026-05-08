package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.sirius.Robot;
import org.firstinspires.ftc.teamcode.sirius.outtake.mechanism.CyliisTuret;

@TeleOp(name = "Test Turret Tunning")
@Config
public class TestTurretTunning extends LinearOpMode {

    Robot robot;
    CyliisTuret turret;

    // Change these from Dashboard
    public static double goalX = 1300;
    public static double goalY = -3100;

    public static boolean resetOdo = false;

    public static boolean runFlywheel = false;
    public static boolean runTurret = true;

    @Override
    public void runOpMode() throws InterruptedException {

        robot = new Robot(hardwareMap);

        // If your Robot already creates the turret, use this:
        turret = robot.outtake.turet;

        // If the line above gives an error, delete it and use this instead:
        // turret = new CyliisTuret(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {

            CyliisTuret.goalX = goalX;
            CyliisTuret.goalY = goalY;

            if (resetOdo) {
                turret.pinPointLocalizer.Reset();
                resetOdo = false;
            }

            if (runTurret) {
                turret.pinPointLocalizer.Update();
                turret.update();
            } else {
                turret.pinPointLocalizer.Update();
            }

            Robot.dash.addData("goalX", goalX);
            Robot.dash.addData("goalY", goalY);

            Robot.dash.addData("robotX", turret.pinPointLocalizer.getCurrentPosition().x);
            Robot.dash.addData("robotY", turret.pinPointLocalizer.getCurrentPosition().y);
            Robot.dash.addData("robotHeading", turret.pinPointLocalizer.getCurrentPosition().h);

            Robot.dash.addData("turret target angle", CyliisTuret.turretTargetAngle);

            Robot.dash.update();
        }
    }
}