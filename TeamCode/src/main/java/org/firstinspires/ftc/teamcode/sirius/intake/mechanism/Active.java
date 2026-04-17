package org.firstinspires.ftc.teamcode.sirius.intake.mechanism;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.sirius.intake.IntakeMap;
import org.firstinspires.ftc.teamcode.sirius.intake.IntakeSettings;

public class Active {
    CRServo motor;

    public Active(HardwareMap hardwareMap) {
        motor = hardwareMap.get(CRServo.class, IntakeMap.active);
    }

    public void intake() {
        motor.setPower(-IntakeSettings.intakeSpeed);
    }

    public void stop() {
        motor.setPower(0.0);
    }

}
