package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
@Config
/* loaded from: classes8.dex */
public class DoubleMotorTest extends LinearOpMode {
    public static String testedMotorOne = "";
    public static String testedMotorTwo = "";
    public static boolean isReversedOne = false;
    public static boolean isReversedTwo = false;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() throws InterruptedException {
        DcMotorEx motorOne = (DcMotorEx) this.hardwareMap.get(DcMotorEx.class, testedMotorOne);
        if (isReversedOne) {
            motorOne.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        DcMotorEx motorTwo = (DcMotorEx) this.hardwareMap.get(DcMotorEx.class, testedMotorTwo);
        if (isReversedTwo) {
            motorTwo.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        waitForStart();
        while (opModeIsActive()) {
            this.telemetry.addData("[STATUS]", "MotorTest Teleop is running.");
            motorOne.setPower(this.gamepad1.right_trigger);
            motorTwo.setPower(this.gamepad1.right_trigger);
            this.telemetry.update();
        }
    }
}
