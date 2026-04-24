package org.firstinspires.ftc.teamcode.sirius.testing;



import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PwmControl;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.sirius.Robot;
import org.firstinspires.ftc.teamcode.sirius.Wrappers.ServoWithPWMChange;

import java.security.CryptoPrimitive;

@TeleOp
@Config
/* loaded from: classes8.dex */
public class DoubleCRSTest extends LinearOpMode {
    DcMotorEx encoder;
    public static Telemetry dash;
    public static String testedMotorOne = "";
    public static String testedMotorTwo = "";
    public static boolean isReversedOne = false;
    public static boolean isReversedTwo = false;
    public static double power = 0.0d;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() throws InterruptedException {
        CRServo motorOne = (CRServo) this.hardwareMap.get(CRServo.class, testedMotorOne);
        dash = FtcDashboard.getInstance().getTelemetry();
        encoder = hardwareMap.get(DcMotorEx.class, "rearLeft");
        if (isReversedOne) {
            motorOne.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        CRServo motorTwo = (CRServo) this.hardwareMap.get(CRServo.class, testedMotorTwo);
        if (isReversedTwo) {
            motorTwo.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("[STATUS]", "MotorTest Teleop is running.");
            motorOne.setPower(power);
            motorTwo.setPower(power);
            dash.addData("rpm",  encoder.getVelocity()/28 * 60);
            dash.addData("POS",  encoder.getCurrentPosition());
            dash.update();
            telemetry.update();
        }
    }
}
