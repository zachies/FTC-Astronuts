
/**
 * EncoderMotor class: making your encoders manageable.
 * Author: Marcus Eliason (Choooooooooooooooooooooooooooooooooooooooooooooooood)
 * Created 10/2/15
 */

package com.qualcomm.ftcrobotcontroller;

//Imports.
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

//Main class.
public class EncoderMotor {

    /*
    Class variables.
     */

    //This stores the motor instance.
    private DcMotor motorMain;

    //Stores the case value.
    private int Case;

    //Stores the current target value.
    private int motorTarget;

    //Stores the accuracy value.
    public int accuracy;

    //This is a flag for the use of the calling program.
    public boolean isDone;

    /*
    Main methods.
     */

    //Constructor.
    public EncoderMotor(DcMotor motorMain, int accuracy){

        this.motorMain = motorMain;
        this.Case = 0;
        this.motorTarget = 0;
        this.accuracy = accuracy;
        this.isDone = false;

        runMode(DcMotorController.RunMode.RESET_ENCODERS);

    }

    //Sets the run mode of the motor.
    public void runMode(DcMotorController.RunMode Mode){

        this.motorMain.setChannelMode(Mode);

    }

    //Tests to see if the motor has been set to the specified mode.
    public boolean cnf(DcMotorController.RunMode Mode){

        boolean Cmp;

        if(this.motorMain.getChannelMode() == Mode){

            Cmp = true;

        }

        else{

            Cmp = false;

        }

        return Cmp;

    }

    //Moves the motors to the desired target with the selected power.
    public void move(int Target, double Power){

        switch(Case){

            //CASE 0: Resets the encoders.
            case 0:

                runMode(DcMotorController.RunMode.RESET_ENCODERS);

                this.Case = 1;

                break;

            //CASE 1: Confirms that the encoders have been reset.
            case 1:

                if(cnf(DcMotorController.RunMode.RESET_ENCODERS)){

                    this.Case = 2;

                }

                break;

            //CASE 2: Sets the motors to the run to position mode.
            case 2:

                runMode(DcMotorController.RunMode.RUN_TO_POSITION);

                this.Case = 3;

                break;

            //CASE 3: Confirms that the motor has been set to the appropriate mode.
            case 3:

                if(cnf(DcMotorController.RunMode.RUN_TO_POSITION)){

                    this.Case = 4;

                }

                this.isDone = true; //Set to true, as a value can now be assigned as the target.

                break;

            //CASE 4: Sets the target position to a value that will not be changed until the target has been reached.
            case 4:

                this.motorTarget = Target;
                this.Case = 5;

                break;

            //CASE 5: Tests to see if the current position is within 5 of the target position.
            //If true, the motor power will be set to zero, and the case will be set to 4 so a new target can be set.
            //If false, the case will be set to 6, so the motor can move to the current target.
            case 5:

                if(Math.abs(this.motorMain.getCurrentPosition()-this.motorTarget)<=this.accuracy){

                    this.motorMain.setPower(0);
                    this.Case = 4;
                    this.isDone = true; //Set to true, as a new value can now be assigned.

                }

                else{

                    this.Case = 6;
                    this.isDone = false; //The target has not been met, so flag is set to false.

                }

                break;

            //CASE 6: Sets the DC motor target to the desired target, the power is also set.
            case 6:

                this.motorMain.setTargetPosition(this.motorTarget);
                this.motorMain.setPower(Power);
                this.Case = 5; //Set to 5, to test if the target has been reached.

                break;

            //In case (Haha) the case value was set out of bounds
            default:

                throw new IndexOutOfBoundsException("Invalid case identifier: " + this.Case);

        }

    }

    //Move the motors with only a power value. (Encoders will still record their current position.)
    public void moveManual(double Power){

        this.Case = 2; //Set to 2, as the run to position mode will need to be set again in the main move method.

        if(cnf(DcMotorController.RunMode.RUN_USING_ENCODERS)){

            this.motorMain.setPower(Power);

        }

        else{

            runMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        }

    }

    //Debug info.
    public String[] debugInfo(){

        String[] debug = new String[5];

        debug[0] = this.motorMain.toString();
        debug[1] = Integer.toString(this.motorMain.getCurrentPosition());
        debug[2] = this.motorMain.getChannelMode().toString();
        debug[3] = Integer.toString(this.Case);
        debug[4] = Integer.toString(this.motorTarget);

        return debug;

    }

    /*
     WARNING: The use of these methods strongly discouraged.
     They are untested, as well as poor coding practice.
     Use at your own risk!
     */

    @Deprecated
    //Changes the current case value from the calling program.
    public void overrideCase(int Case){

        this.Case = Case;

    }

    @Deprecated
    //Changes the target value, even if the current target has not been met.
    public void overrideTarget(int Target){

        this.motorTarget = Target;

    }

    @Deprecated
    //Changes the motor called by the encoder program.
    public void overrideMotor(DcMotor motorMain){

        this.motorMain = motorMain;

    }

}