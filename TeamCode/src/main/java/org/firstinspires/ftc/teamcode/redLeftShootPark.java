package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.groups.Groups;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.*;
import static com.pedropathing.ivy.pedro.PedroCommands.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.LinkedHashMap;
import java.util.function.BooleanSupplier;

@Autonomous(name = "[RED LEFT] Shoot and Park", group = "Autonomous")
public class redLeftShootPark extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    // poses (points where the robot goes)
    private final Pose start = poseFactory.of(8.66, 132.61, 0);
    private final Pose path1 = poseFactory.of(57.4521, 120.7125, 270);
    private final Pose point2Start = poseFactory.of(57.4521, 120.7125, 270);
    private final Pose point2 = poseFactory.of(9.7597, 91.9499, 270);

    // Autonomous routine

    @Override
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();

        DcMotorEx launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        CRServo windmillServo = hardwareMap.get(CRServo.class, "windmillServo");

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));

        Command runWindmill = instant(() -> windmillServo.setPower(1));
        Command stopWindmill = instant(() -> windmillServo.setPower(0));
        Command runLauncher = instant(() -> launcher.setVelocity(OpModeStorage.LAUNCHER_TARGET_VELOCITY));
        Command stopLauncher = instant(() -> launcher.setVelocity(0));
        Command allowLaunchTime = waitMs(5000);

        LinkedHashMap<BooleanSupplier, Command> cases = new LinkedHashMap<>();
        cases.put(() -> launcher.getVelocity() >= OpModeStorage.LAUNCHER_MIN_VELOCITY, runWindmill);
        cases.put(() -> launcher.getVelocity() <= OpModeStorage.LAUNCHER_MIN_VELOCITY, stopWindmill);


        // This is the sequence of commands that will be run IN ORDER
        Command autoRoutine = sequential(
                follow(follower, path1()),
                runLauncher,
                allowLaunchTime,
                stopLauncher,
                follow(follower, path2())
        );

        Command handleWindmill = branch(cases);
        Command windmillLoop = Groups.loop(handleWindmill);

        waitForStart();
        schedule(windmillLoop);
        schedule(autoRoutine);

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            telemetry.update();
        }

        OpModeStorage.autonomousEndPose = follower.pose(); //saves your position in that file
    }


    public Path path1() {
        return Paths.line(start, path1).linear(start, path1);
    }

    public Path path2() {
        return Paths.line(point2Start, point2).linear(point2Start, point2);
    }
}

// lane was here