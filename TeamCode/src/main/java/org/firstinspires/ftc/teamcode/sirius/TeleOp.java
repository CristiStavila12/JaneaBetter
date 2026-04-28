package org.firstinspires.ftc.teamcode.sirius;



import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.sirius.util.StickyGamepad;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "*TeleOp*")
@Config
/* loaded from: classes9.dex */
public class TeleOp extends LinearOpMode {
    public static StickyGamepad stickyGamepad;
    public ChassisController chassisController;
    Robot robot;


    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() throws InterruptedException {
        chassisController = new ChassisController(hardwareMap);
        robot = new Robot(this.hardwareMap);
        stickyGamepad = new StickyGamepad(gamepad1, this);
        robot.init();
        waitForStart();
        while (opModeIsActive()) {
            robot.autoIntake();
            robot.update();
            chassisController.updateMovement(gamepad1);
            stickyGamepad.update();

            if (stickyGamepad.x) {
                robot.toggleIntake();
            }
            if (stickyGamepad.right_bumper){
                robot.startScore = true;
                robot.timer.reset();
            }

            telemetry.addData("color", robot.colorRangeSensor.getColorSeenBySensor());
            Robot.dash.addData("x", robot.outtake.turetOdometry.pinPointLocalizer.getCurrentPosition().x);
            Robot.dash.addData("y", robot.outtake.turetOdometry.pinPointLocalizer.getCurrentPosition().y);
            Robot.dash.addData("h", robot.outtake.turetOdometry.pinPointLocalizer.getCurrentPosition().h);
            Robot.dash.addData("color", robot.colorRangeSensor.getColorSeenBySensor());
            Robot.dash.addData("spindexPos", robot.intake.spindex.getCurrentPosition());
            Robot.dash.addData("dist", robot.colorRangeSensor.getDistance(DistanceUnit.CM));
            Robot.dash.addData("targetPos", robot.intake.spindex.pos);
            Robot.dash.addData("sorterStateCS", robot.sorterStateCS);
            Robot.dash.addData("2nd", robot.secondSorterCS);
            Robot.dash.addData("3rd", robot.thirdSorterCS);
            Robot.dash.update();
            telemetry.update();
        }

    }
}

