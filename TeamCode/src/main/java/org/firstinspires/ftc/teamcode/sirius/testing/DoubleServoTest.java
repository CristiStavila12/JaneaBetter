package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
@Config
/* loaded from: classes8.dex */
public class DoubleServoTest extends LinearOpMode {
    public static String servoOne = "";
    public static String servoTwo = "";
    public static double servoOnePosition = 0.5d;
    public static double servoTwoPosition = 0.5d;
    public static double increment = 0.001d;
    public static boolean simultaneousPositions = false;
    public static double sharedPosition = 0.5d;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() throws InterruptedException {
        Servo testedServoOne = (Servo) this.hardwareMap.get(Servo.class, servoOne);
        Servo testedServoTwo = (Servo) this.hardwareMap.get(Servo.class, servoTwo);
        waitForStart();
        while (opModeIsActive()) {
            this.telemetry.addData("[STATUS]", "DoubleServoTest Teleop is running.");
            if (this.gamepad1.dpad_up && servoOnePosition <= 1.0d) {
                servoOnePosition += increment;
            } else if (this.gamepad1.dpad_down && servoOnePosition >= 0.0d) {
                servoOnePosition -= increment;
            }
            if (this.gamepad1.y && servoTwoPosition <= 1.0d) {
                servoTwoPosition += increment;
            } else if (this.gamepad1.a && servoTwoPosition >= 0.0d) {
                servoTwoPosition -= increment;
            }
            if (simultaneousPositions) {
                servoOnePosition = sharedPosition;
                servoTwoPosition = sharedPosition;
                this.telemetry.addData("[INFO]", "Servo Shared Position: " + sharedPosition);
            }
            testedServoOne.setPosition(servoOnePosition);
            testedServoTwo.setPosition(servoTwoPosition);
            this.telemetry.addData("[INFO]", "Servo [1] Position: " + testedServoOne.getPosition());
            this.telemetry.addData("[INFO]", "Servo [2] Position: " + testedServoTwo.getPosition());
            this.telemetry.update();
        }
    }
}
