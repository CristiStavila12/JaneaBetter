package org.firstinspires.ftc.teamcode.sirius;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

/* loaded from: classes9.dex */
public class ChassisController {
    DcMotorEx leftFrontMotor;
    DcMotorEx leftRearMotor;
    DcMotorEx rightFrontMotor;
    DcMotorEx rightRearMotor;
    public static double speed = 1.0d;
    public static double rotSpeed = 1.0d;

    public ChassisController(HardwareMap hardwareMap) {
        this.leftFrontMotor = hardwareMap.get(DcMotorEx.class, "frontLeft");
        this.rightFrontMotor =  hardwareMap.get(DcMotorEx.class, "frontRight");
        this.leftRearMotor =  hardwareMap.get(DcMotorEx.class, "rearLeft");
        this.rightRearMotor =  hardwareMap.get(DcMotorEx.class, "rearRight");
        this.leftFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.leftRearMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.rightFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.rightRearMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.leftFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.rightFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.leftRearMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.rightRearMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.leftFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.rightFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.leftRearMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.rightRearMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void updateMovement(Gamepad g) {
        double y = g.left_stick_y;
        double x = g.left_stick_x * 1.1d;
        double rx = g.right_stick_x;
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0d);
        double frontLeftPower = ((y + x) + rx) / denominator;
        double backLeftPower = ((y - x) + rx) / denominator;
        double frontRightPower = ((y - x) - rx) / denominator;
        double backRightPower = ((y + x) - rx) / denominator;
        this.leftFrontMotor.setPower(frontLeftPower);
        this.leftRearMotor.setPower(backLeftPower);
        this.rightFrontMotor.setPower(frontRightPower);
        this.rightRearMotor.setPower(backRightPower);
    }
}
