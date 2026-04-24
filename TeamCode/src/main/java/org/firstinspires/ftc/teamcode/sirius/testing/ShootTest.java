package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.sirius.Robot;

@TeleOp
@Config
/* loaded from: classes8.dex */
public class ShootTest extends LinearOpMode {
    CRServo outtakeRight;
    CRServo outtakeLeft;
    CRServo spindex;
    Servo arm;
    public static double power1;
    public static double power2;
    public static double power3;
    public static double servoPos;


    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() throws InterruptedException {
        outtakeRight = hardwareMap.get(CRServo.class, "outtakeRight");
        outtakeLeft = hardwareMap.get(CRServo.class, "outtakeLeft");
        spindex = hardwareMap.get(CRServo.class, "spindex");
        arm = hardwareMap.get(Servo.class, "arm");
        waitForStart();
        while (opModeIsActive()) {
            outtakeRight.setPower(power1);
            outtakeLeft.setPower(power2);
            spindex.setPower(power3);
            arm.setPosition(servoPos);
        }
    }
}
