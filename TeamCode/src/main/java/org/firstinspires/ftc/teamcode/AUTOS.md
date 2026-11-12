# AUTOS
### 1. Make your pathing in Pedro Pathing Visualizer
This is fairly simple. Make sure your path to get to launch position is named `positionForLaunch` and the park path is named `goPark`

### 2. Make a new Java Class
Right click `org.firstinspires.ftc.teamcode` in Android Studio and click New, then Java Class.

The naming scheme is as follows:  
sideABILITIESPark

**side:** Side where start location is relative to Drive Team  
**ABILITIES:** letter for each ability, L for leave, S for shoot, P for park, in order of LSP  
**Park:** High: majority in red square

Side should be fully lowercase, abilities all uppercase, park should have first letter capitalized

### 3. Paste in Visualizer code
Paste in the code you get from the visualizer by going to `Export>Export java code` then click the drop down for full code and click `Copy`

Then replace all the code in the new class you made with that.
For any red lines under any lines of code look for the solution it provides for you (either will be visible or you will have to click a lightbulb)   

`@Autonomous(name = "RIGHT FULL Low park", group = "Autonomous")`  
Look for this line. Change the name quotes to the name you want.  

`public class rightLSPHigh extends LinearOpMode {`   
Look for this line. Change `rightLSPHigh` to the name you set for the Java Class, not the name you set in the text above this one.

You also need to paste `import org.firstinspires.ftc.teamcode.pedro.Constants;` with all the other imports at the top too.

### 4. Place prebuilt code
Remove the following code:
```java
// Autonomous routine
    public Command autoRoutine() {
        return sequential(
            follow(follower, positionForLaunch()),
            follow(follower, goPark())
        );
    }
```
Then, replace:
```java
        waitForStart();
        schedule(autoRoutine());
```
with the following code:
```java
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
```

If you have no red lines, **congrats, you did it right!** However, this is no means a finished auto. The wait time may need to be changed, or more waits may need to be added. You should read the Pedro Pathing and Ivy documentation.