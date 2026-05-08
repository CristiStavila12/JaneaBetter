package org.firstinspires.ftc.teamcode.sirius.Wrappers;

public class Pose2D {
    public double x;
    public double y;
    public double heading;

    Pose2D correction;

    public Pose2D(double x, double y) {
         this.x = x;
         this.y = y;
         this.heading = 0;
    }

    public Pose2D(double x , double y , double heading)
    {
        this.x=x;
        this.y=y;
        this.heading=heading;
    }

    public void correct()
    {
        x+=correction.x;
        y+=correction.y;
        heading+=correction.heading;
    }

    public Pose2D(double x , double y , double heading , Pose2D correction)
    {
        this.x=x;
        this.y=y;
        this.heading=heading;
        this.correction=correction;
    }
    public Pose2D(){}

    public void rotate(double a) {
        double newX = Math.cos(a) * x - Math.sin(a) * y;
        double newY = Math.sin(a) * x + Math.cos(a) * y;

        this.x = newX;
        this.y = newY;
    }

}