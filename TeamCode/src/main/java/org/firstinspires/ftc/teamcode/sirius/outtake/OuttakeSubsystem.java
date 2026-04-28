package org.firstinspires.ftc.teamcode.sirius.outtake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.sirius.outtake.mechanism.Arm;
import org.firstinspires.ftc.teamcode.sirius.outtake.mechanism.OuttakeShooter;
import org.firstinspires.ftc.teamcode.sirius.outtake.mechanism.PinPointLocalizer;
import org.firstinspires.ftc.teamcode.sirius.outtake.mechanism.TuretOdometry;

public class OuttakeSubsystem {
    public Arm arm;
    public PinPointLocalizer pinPointLocalizer;
    public TuretOdometry turetOdometry;
    public OuttakeShooter outtakeShooter;
    public OuttakeSubsystem(HardwareMap hardwareMap){
        arm = new Arm(hardwareMap);
        pinPointLocalizer = new PinPointLocalizer(hardwareMap);
        turetOdometry = new TuretOdometry(hardwareMap);

    }

    public void score(){
        arm.goToScore();
    }
}
