package org.firstinspires.ftc.teamcode.sirius.intake.mechanism;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.sirius.intake.IntakeMap;
import org.firstinspires.ftc.teamcode.sirius.intake.IntakeSettings;
import org.firstinspires.ftc.teamcode.sirius.util.PIDController;

public class Spindex {
    public CRServo spindex;
    public AnalogInput spindexEncoder;
    public double power;
    public double pos;
    public PIDController spindexPid = new PIDController(1, 0, 0.085);
    public Spindex(HardwareMap hardwareMap){
        spindex = hardwareMap.get(CRServo.class, IntakeMap.spindex);
        spindexEncoder = hardwareMap.get(AnalogInput.class, IntakeMap.spindexEncoder);
    }
    public double getCurrentPosition(){
        return spindexEncoder.getVoltage()/3.3 * 2*Math.PI;
    }

    public void goToCollect1(){
        pos = IntakeSettings.collect1;
    }
    public void goToCollect2(){
        pos = IntakeSettings.collect2;
    }

    public void goToCollect3(){
        pos = IntakeSettings.collect3;
    }
    public void goToScore1(){
        pos = IntakeSettings.score1;
    }
    public void goToScore2(){
        pos = IntakeSettings.collect2;
    }
    public void goToScore3(){
        pos = IntakeSettings.score3;
    }

}
