package org.firstinspires.ftc.teamcode.sirius.Wrappers;

import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ColorRangeSensorWraper {
    ColorRangeSensor colorRange;

    public class ColorRangeSensorPacket {
        public double R, G, B, A;
        public NormalizedRGBA colors;
        public double D;
        public ColorRangeSensorPacket(){
            R = G = B = 0;
            D = 0;
        }
        @Override
        public String toString(){
            return Double.toString(R) + ' ' + Double.toString(G) + ' ' + Double.toString(B);
        }
    }
    private long timeDistance = 0, timeRGB = 0;
    private double freq = 20;
    private double lowPassFilter = 1;
    public ColorRangeSensorPacket p = new ColorRangeSensorPacket();
    public ColorRangeSensorPacket RGB = new ColorRangeSensorPacket();

    public ColorRangeSensorWraper(String Name, HardwareMap hm){
        colorRange = hm.get(ColorRangeSensor.class,Name);
        freq = 50;
    }

    public Colors.ColorType getColorSeenBySensor(){
        try {
            if (System.currentTimeMillis() - timeRGB > freq) {
                RGB.colors = colorRange.getNormalizedColors();
                float scale = 256.f;

                RGB.R = Range.clip(RGB.colors.red * scale, 0, 255);
                RGB.G = Range.clip(RGB.colors.green * scale, 0, 255);
                RGB.B = Range.clip(RGB.colors.blue * scale, 0, 255);
                RGB.D = colorRange.getDistance(DistanceUnit.CM);

                timeRGB = System.currentTimeMillis();
            }
        } catch (Exception ignored){
            RobotLog.ii("cur mort de senzor","mort");
        }
        return Colors.getArtefactColor(new Colors.Color(RGB.R, RGB.G, RGB.B, RGB.D));
    }

    public double getDistance(DistanceUnit unit){
        return colorRange.getDistance(unit);
    }

}
