package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.teamcode.sirius.Robot;

@TeleOp
@Config
/* loaded from: classes8.dex */
public class TestTurretTunning extends LinearOpMode {
    public static PIDCoefficients coefs = new PIDCoefficients(0.0, 0.0, 0.0);
    Robot robot;
    org.firstinspires.ftc.teamcode.sirius.outtake.mechanism.TuretOdometry turetOdometry;

    public static boolean reset = false;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() throws InterruptedException {
        this.turetOdometry = new org.firstinspires.ftc.teamcode.sirius.outtake.mechanism.TuretOdometry(this.hardwareMap);
        this.robot = new Robot(this.hardwareMap);
        turetOdometry.turretEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turetOdometry.turretEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        waitForStart();
        while (opModeIsActive()) {
            if (reset){
                turetOdometry.turretEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                turetOdometry.turretEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                reset = false;
            }

            Robot.dash.addData("currPos", turetOdometry.turretEncoder.getCurrentPosition());
            Robot.dash.addData("heading", turetOdometry.robotHeading);
            Robot.dash.addData("currentPos", turetOdometry.currentTurretRel);
            Robot.dash.addData("targetPos", turetOdometry.ShouldHaveTurretHeading);
            Robot.dash.addData("targetPos2", turetOdometry.targetGlobalHeading);
            Robot.dash.update();

            this.turetOdometry.updateFacingDirection();
            this.turetOdometry.turretController.setPidCoefficients(coefs);
            this.turetOdometry.pinPointLocalizer.Update();
        }
    }
}
