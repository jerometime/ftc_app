package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Game Pad TeleOp", group="Linear Opmode")
public class GamePadTeleOp extends LinearOpMode {
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightFront = null;
    private DcMotor rightBack = null;

    private DcMotor armMain = null;
    private Servo clawL = null;
    private Servo clawR = null;

    private void ctrlLeft(double pow) {
        leftFront.setPower(-pow);
        leftBack.setPower(-pow);
    }

    private void ctrlRight(double pow) {
        rightFront.setPower(pow);
        rightBack.setPower(pow);
    }

    private void ctrlArm(double pow) {
        /*
         * Encoder values:
         * Side 1:
         * Meridian:
         * Side 2:
         */
        armMain.setPower(pow);
    }

    private void ctrlClaw(boolean open) {
        double leftPos;
        double rightPos;
        leftPos = clawL.getPosition();
        rightPos = clawR.getPosition();
        if (open) {
            // Operate servo in opening direction.
            clawL.setPosition(leftPos + 0.02);
            clawR.setPosition(rightPos - 0.02);
        } else {
            // Operate servo in closing direction.
            clawL.setPosition(leftPos - 0.02);
            clawR.setPosition(rightPos + 0.02);
        }
    }

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFront = hardwareMap.get(DcMotor.class, "left_front");
        leftBack = hardwareMap.get(DcMotor.class, "left_back");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        rightBack = hardwareMap.get(DcMotor.class, "right_back");

        armMain = hardwareMap.get(DcMotor.class, "arm_main");
        clawL = hardwareMap.get(Servo.class, "claw_left");
        clawR = hardwareMap.get(Servo.class, "claw_right");

        //armMain.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double leftPower;
            double rightPower;

            double armPower;
            double armRev;

            // Choose to drive using either Tank Mode, or POV Mode
            // Comment out the method that's not used.  The default below is POV.

            // POV Mode uses left stick to go forward, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive = gamepad1.left_stick_y;
            double turn  = gamepad1.right_stick_x;
            leftPower    = Range.clip(drive + turn, -0.9, 0.9) ;
            rightPower   = Range.clip(drive - turn, -0.9, 0.9) ;

            armPower = Range.clip(-gamepad2.left_stick_y, -0.5, 0.5);
            //armRev = armMain.getCurrentPosition();

            double clawV = gamepad2.right_stick_y;
            if (clawV > 0.5) {
                ctrlClaw(true);
            } else if (clawV < -0.5) {
                ctrlClaw(false);
            }

            // Tank Mode uses one stick to control each wheel.
            // - This requires no math, but it is hard to drive forward slowly and keep straight.
            // leftPower  = -gamepad1.left_stick_y ;
            // rightPower = -gamepad1.right_stick_y ;

            // Send calculated power to wheels
            ctrlLeft(leftPower);
            ctrlRight(rightPower);
            ctrlArm(armPower);

            // Show the elapsed game time and
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Drive Power", "left (%.2f), right (%.2f)", leftPower, rightPower);
            telemetry.addData("Arm Power", "(%.2f)", armPower);
            telemetry.addData("Servo Pos", "left (%.2f), right (%.2f)", clawL.getPosition(), clawR.getPosition());
            telemetry.update();
        }
    }
}
