package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.sirius.Robot;

@TeleOp
@Config
/* loaded from: classes8.dex */
public class MotorTest extends LinearOpMode {
    Robot robot;
    public double lastPosition;
    public static String motorToTest = "";
    public static boolean isReversed = false;
    public static double power = 0.0d;
    ElapsedTime elapsedTime;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v2, types: [void] */
    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() throws InterruptedException {
        robot = new Robot(hardwareMap);
        elapsedTime = new ElapsedTime();
        new Object();
        waitForStart();
        while (opModeIsActive()) {
            DcMotorEx testedMotor = hardwareMap.get(DcMotorEx.class, motorToTest);
            if (isReversed) {
                testedMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            }



            elapsedTime.reset();
            testedMotor.setPower(power);
            Robot.dash.update();


        }
    }
}
