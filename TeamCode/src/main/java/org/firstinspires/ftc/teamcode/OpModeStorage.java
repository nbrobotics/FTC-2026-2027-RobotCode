package org.firstinspires.ftc.teamcode;
//Make sure this matches your file location

import com.pedropathing.math.Pose;

public class OpModeStorage {
    public static Pose autonomousEndPose = new Pose(8.66, 8.89, 0);
    // default value to prevent null errors

    public static int LAUNCHER_TARGET_VELOCITY = 1273; //2728 RPM
    public static int LAUNCHER_MIN_VELOCITY = 1260; //2700 RPM
}