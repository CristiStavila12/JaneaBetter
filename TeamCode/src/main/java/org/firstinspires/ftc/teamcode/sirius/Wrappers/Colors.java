package org.firstinspires.ftc.teamcode.sirius.Wrappers;

import com.acmerobotics.dashboard.config.Config;

@Config
public class Colors {
    public static class Color{
        public double r, g, b, d;
        public Color(double r, double g, double b, double d){
            this.r = r;
            this.g = g;
            this.b = b;
            this.d = d;
        }

    }
    public enum ColorType {

        GREEN(new Color(0.2, 0.9, 0.7, 3.8)),
        PURPLE(new Color(0.6, 0.7, 1.2, 3.8)),
        NONE(new Color(0.1, 0.2, 0.2, 4));


        private final Color color;

        ColorType(Color c) {
            this.color = c;
        }

        public Color getColor(){ return color; }

    }
    public static double getColorDistance(Color c1, Color c2) {
        double rDiff = c1.r - c2.r;
        double gDiff = c1.g - c2.g;
        double bDiff = c1.b - c2.b;
        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }
    public static ColorType getArtefactColor(Color input){
        if (input.d > 1) return ColorType.NONE;
        double greenDist = getColorDistance(input, ColorType.GREEN.getColor());
        double purpleDist = getColorDistance(input, ColorType.PURPLE.getColor());
        if (greenDist> purpleDist) return ColorType.PURPLE;
        return ColorType.GREEN;
    }

}