package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.sirius.Robot;
import org.firstinspires.ftc.teamcode.sirius.Wrappers.ColorRangeSensorWraper;


@TeleOp
@Config
public class ColorSensor extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        ColorRangeSensorWraper colorSensor = new ColorRangeSensorWraper("intakeColorSensor", hardwareMap);
        Robot robot = new Robot(hardwareMap);
        while (opModeInInit()){
            Robot.dash.addData("r", colorSensor.RGB.R);
            Robot.dash.addData("g", colorSensor.RGB.G);
            Robot.dash.addData("b", colorSensor.RGB.B);
            Robot.dash.addData("a", colorSensor.RGB.A);
            Robot.dash.addData("d", colorSensor.getDistance(DistanceUnit.CM));
            Robot.dash.update();
            colorSensor.getColorSeenBySensor();
        }
    }
}
