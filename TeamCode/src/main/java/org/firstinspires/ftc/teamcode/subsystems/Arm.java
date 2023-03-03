package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.commands.RunMode.*;

import com.arcrobotics.ftclib.command.Subsystem;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.*;

import org.firstinspires.ftc.teamcode.commands.*;

public class Arm implements Subsystem {
    private Servo leftArm, rightArm;
    private double INTAKING = 5, GROUND = 5, LIFTED = 45, SCORING = 182, BACKWARDS = 295, LEVEL = 210, VERTICAL = 130;
    private double P = 1, I = 1, D = 1, tolerance = 1;
    double currentTarget, set, finalTarget;
    PIDController controller;
    private double update = 0.05;

    Double goTo;
    TrapezoidalMotionProfile profile;
    private RunMode runMode;

    public Arm(HardwareMap hardwareMap){
        rightArm = hardwareMap.servo.get("rightArm");
        leftArm = hardwareMap.servo.get("leftArm");
        rightArm.setDirection(Servo.Direction.REVERSE);

        //Max accel: 100°/s Max vel: 500°/s
        controller = new PIDController(P, I, D);
        controller.setTolerance(tolerance);
        runMode = RunMode.TELE;
    }

    public void setAutoPositions(int pos){
        runMode = AUTO;
        SCORING = pos;
    }

    public void customAutoPosition(double position){ SCORING = position;}

    public void setPosition(State state){
        switch(state){
            case INTAKING:
                setArms(INTAKING);
                break;
            case GROUND:
                setArms(GROUND);
                break;
            case LOW:
                setArms(LEVEL);
                break;
            case BACKWARDS:
                setArms(BACKWARDS);
                break;
            case LIFTED:
                setArms(LIFTED);
                break;
            default:
                setArms(SCORING);
        }
    }

    public void slamThatJawn(){
        setArms(SCORING+30);
    }

    public void setArms(double target){
        finalTarget = target;
        switch(runMode) {
            case AUTO:
                set = toServoPosition(target);
                break;
            case TELE:
                double pid = controller.calculate(leftArm.getPosition(), toAngle(target));

                if (controller.atSetPoint()) {
                    currentTarget = target;
                } else {
                    currentTarget += pid;
                }
                set = currentTarget;
        }
        leftArm.setPosition(set);
        rightArm.setPosition(set);
    }

    public void updateArms(){
        setArms(finalTarget);
    }

    public double toServoPosition(double angle){
        return (angle/355);
    }

    public double toAngle(double position){
        return position * 355;
    }

    public void setVertical(){ setArms(VERTICAL); }

    //public double toAxonPosition(double angle) { return (angle/(360*5))*0.85;} //5 turn?
    //public double toServoPosition(double angle){
    //        return angle/(300); //gobilda torque
    //    }

    /*
    public String armReport(){
        return goTo.toString() + " " + profileStartPosition + " " + profileTravelDistance + " " + (System.currentTimeMillis() - profileStartTime)/1000;
    }
    */

}
