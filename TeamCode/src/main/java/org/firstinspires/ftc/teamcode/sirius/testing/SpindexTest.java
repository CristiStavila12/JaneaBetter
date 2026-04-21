package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.sirius.Robot;
import org.firstinspires.ftc.teamcode.sirius.util.PIDController;

@TeleOp
@Config
public class SpindexTest extends LinearOpMode {
    Robot robot;
    public static PIDCoefficients coefs = new PIDCoefficients(0,0,0);
    public PIDController spindexPID = new PIDController(0,0,0);
    public static double pos = 0.0;

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new Robot(hardwareMap);
        waitForStart();
        while (opModeIsActive()){
            spindexPID.setPidCoefficients(coefs);
            double power = spindexPID.calculatePower(robot.intake.spindex.getCurrentPosition() - pos);

        robot.intake.spindex.spindex.setPower(power);
        Robot.dash.addData("currPos", robot.intake.spindex.getCurrentPosition());
        Robot.dash.update();
        }
    }
}
