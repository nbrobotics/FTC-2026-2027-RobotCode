package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("left_front_drive");
        c.frontRightName.set("right_front_drive");
        c.backLeftName.set("left_back_drive");
        c.backRightName.set("right_back_drive");
        c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.frontRightDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        c.xPodOffset.set(6.194142619455894);
        c.yPodOffset.set(-6.711785624346396);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.49889314098596477);
                Controller secondaryTranslationalForward = Controller.proportional(0.1843276849240724);
                Controller primaryTranslationalLateral = Controller.proportional(0.3304930456618351);
                Controller secondaryTranslationalLateral = Controller.proportional(0.12210834943522629);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.015602578797597908));
                c.brake.set(Controller.proportionalFeedforward(0.013262191977958223));

                c.headingFeedback.set(Controller.proportional(3.4654129518624535));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.042004810426151645, 0.005371946189032125));

                c.linearBrakeCoefficients.set(Matrix.diag(0.05587927471572838, 0.055667885026347556));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0013635721692877318, 0.0015181770554949388));

                c.maxAchievableForwardVelocity.set(66.32154189604987);
                c.maxAchievableStrafeVelocity.set(56.921866427354004);
                c.naturalForwardDeceleration.set(42.358157108998974);
                c.naturalStrafeDeceleration.set(59.30066979875795);
            }
    );
    public static Follower create(HardwareMap h) {
        // return new Follower(Drivetrain, Localizer, Foresight);

        return null;
    }
}