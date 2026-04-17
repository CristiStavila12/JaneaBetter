package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
@Config
/* loaded from: classes8.dex */
public class ServoTest extends LinearOpMode {
    public static String servoToTest = "";
    public static double position = 0.5d;
    public static double increment = 0.01d;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v2, types: [void] */
    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() throws InterruptedException {
//        ?? r3;
        Servo testedServo = (Servo) this.hardwareMap.get(Servo.class, servoToTest);
        new Object();
//        this.telemetry = new MultipleTelemetry(this.telemetry, r3);
        waitForStart();
        while (opModeIsActive()) {
            this.telemetry.addData("[STATUS]", "ServoTest Teleop is running.");
            if (this.gamepad1.dpad_up && position <= 1.0d) {
                position += increment;
            } else if (this.gamepad1.dpad_down && position >= 0.0d) {
                position -= increment;
            }
            testedServo.setPosition(position);
            this.telemetry.addData("[INFO]", "Servo Position: " + position);
            this.telemetry.update();
        }
    }
}
