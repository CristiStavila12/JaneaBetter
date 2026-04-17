package org.firstinspires.ftc.teamcode.sirius;



import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

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

        waitForStart();
        while (opModeIsActive()) {
            chassisController.updateMovement(gamepad1);
            stickyGamepad.update();

            if (stickyGamepad.x) {
                robot.toggleIntake();
            }

            telemetry.addData("color", robot.colorRangeSensor.getColorSeenBySensor());
            Robot.dash.addData("color", robot.colorRangeSensor.getColorSeenBySensor());
            Robot.dash.update();
            telemetry.update();
        }

    }
}

