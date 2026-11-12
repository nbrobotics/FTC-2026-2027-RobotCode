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
import static com.pedropathing.ivy.commands.Commands.branch;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.LinkedHashMap;
import java.util.function.BooleanSupplier;

@Autonomous(name = "RIGHT FULL High park", group = "Autonomous")
public class rightLSPHigh extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(55.75, 8.75, 90);
    private final Pose positionForLaunch = poseFactory.of(57.75, 31.75, 90);
    private final Pose goParkStart = poseFactory.of(57.75, 31.75, 90);
    private final Pose goPark = poseFactory.of(10.75, 121.75, 0);
    private final Pose goParkControl1 = poseFactory.of(11.75, 39.75, 0);


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

        Command runWindmill = instant(() -> windmillServo.setPower(-1));
        Command stopWindmill = instant(() -> windmillServo.setPower(0));
        Command runLauncher = instant(() -> launcher.setVelocity(OpModeStorage.LAUNCHER_TARGET_VELOCITY));
        Command stopLauncher = instant(() -> launcher.setVelocity(0));
        Command allowLaunchTime = waitMs(5000);

        LinkedHashMap<BooleanSupplier, Command> cases = new LinkedHashMap<>();
        cases.put(() -> launcher.getVelocity() >= OpModeStorage.LAUNCHER_MIN_VELOCITY, runWindmill);
        cases.put(() -> launcher.getVelocity() <= OpModeStorage.LAUNCHER_MIN_VELOCITY, stopWindmill);

        Command handleWindmill = branch(cases);
        Command windmillLoop = Groups.loop(handleWindmill);

        Command autoRoutine = sequential(
                follow(follower, positionForLaunch()),
                runLauncher,
                allowLaunchTime,
                stopLauncher,
                follow(follower, goPark())
        );

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
    }

    public Path positionForLaunch() {
        return Paths.line(start, positionForLaunch).linear(start, positionForLaunch);
    }

    public Path goPark() {
        return Paths.curve(goParkStart, goParkControl1, goPark).linear(goParkStart, goPark);
    }
}
