package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.sirius.Robot;

@TeleOp
@Config
/* loaded from: classes8.dex */
public class CRSTest extends LinearOpMode {
    public double lastPosition;
    public static String motorToTest = "";
    public static boolean isReversed = false;
    public static double power = 0.0d;
    ElapsedTime elapsedTime;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() throws InterruptedException {
        waitForStart();
        while (opModeIsActive()) {
            CRServo testedMotor = hardwareMap.get(CRServo.class, motorToTest);
            if (isReversed) {
                testedMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            }

            testedMotor.setPower(power);


        }
    }
}
