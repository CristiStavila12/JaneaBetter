package org.firstinspires.ftc.teamcode.sirius.outtake.mechanism;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.sirius.outtake.OuttakeMap;
import org.firstinspires.ftc.teamcode.sirius.outtake.OuttakeSettings;

public class Arm {
    Servo arm;
    public Arm(HardwareMap hardwareMap){
        arm = hardwareMap.get(Servo.class, OuttakeMap.arm);
    }
    public void goToIdle(){
        arm.setPosition(OuttakeSettings.armPasive);
    }
    public void goToScore(){
        arm.setPosition(OuttakeSettings.armEngaged);
    }
}
