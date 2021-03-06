package org.firstinspires.ftc.teamcode.Testing;

import android.graphics.Bitmap;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * Created by RoboticsUser on 10/13/2016.
 */

@Autonomous (name = "Auto_Blue", group = "")
public class DR_Auto_Blue extends Camera_Testing {

    DcMotor motorLF;
    DcMotor motorLB;
    DcMotor motorRF;
    DcMotor motorRB;
    //Servo bBP;
    ColorSensor colorSensorL;
    ColorSensor colorSensorR;
    //OpticalDistanceSensor ODS;

    enum States {INIT_MOTORS, TURN, DRIVE_FORWARD1, TURN_TO_LINE1, TURN_TO_LINE2, LINE_FOLOWING, CAMERA, PRESS_BUTTON}

    States state;

    float L;
    float R;
    double O;
    final int WhiteMaxValR = 716;
    final int WhiteMinValR = 700;
    final int WhiteMaxValL = 1084;
    final int WhiteMinValL = 1070;

    private int ds2 = 2;
    int bBPP;
    int followCnt = 100000000;

    public void init() {

        motorLB = hardwareMap.dcMotor.get("motorLB");
        motorLF = hardwareMap.dcMotor.get("motorLF");
        motorRB = hardwareMap.dcMotor.get("motorRB");
        motorRF = hardwareMap.dcMotor.get("motorRF");
        //bBP = hardwareMap.servo.get("bBP");
        colorSensorR = hardwareMap.colorSensor.get("colorSensorR");
        colorSensorL = hardwareMap.colorSensor.get("colorSensorL");
        //ODS = hardwareMap.opticalDistanceSensor.get("ODS");

        //colorSensorL = hardwareMap.colorSensor.get("colorSensorL");
        colorSensorL.setI2cAddress(I2cAddr.create7bit(0x1e)); //0x3c - new, Port 0
        //colorSensorR = hardwareMap.colorSensor.get("colorSensorR");
        colorSensorR.setI2cAddress(I2cAddr.create7bit(0x26)); //0x4c - old, Port 2

        motorLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        state = States.INIT_MOTORS;



        setCameraDownsampling(2);
        super.init();

    }

    public void loop()
    {
        L = readAvgHue(colorSensorL);
        R = readAvgHue(colorSensorR);
        //O = ODS.getRawLightDetected();
        telemetry.addData("Current State", state);

        switch (state) {

            case INIT_MOTORS:
                motorRF.setPower(-.25);
                motorRB.setPower(-.25);
                motorLF.setPower(.25);
                motorLB.setPower(.25);
                state = States.DRIVE_FORWARD1;
                break;

            case DRIVE_FORWARD1:
                if (motorLB.getCurrentPosition() > 3000)
                {

                    motorRF.setPower(0.0);
                    motorRB.setPower(0.0);
                    motorLF.setPower(0.0);
                    motorLB.setPower(0.0);
                    state = States.TURN;


                }
                else
                {
                    telemetry.addData("Position LF", motorLF.getCurrentPosition());
                    telemetry.addData("Position LB", motorLB.getCurrentPosition());
                    telemetry.addData("Position RF", motorRF.getCurrentPosition());
                    telemetry.addData("Position RB", motorRB.getCurrentPosition());


                }
                break;
            case TURN:
                if(motorLB.getCurrentPosition() <= 4500)
                {
                    motorLF.setPower(0.25);
                    motorRF.setPower(0.0);
                    motorLB.setPower(0.25);
                    motorRB.setPower(0.0);
                    //telemetry.addData("Case:","Turn");
                    telemetry.addData("Position LF", motorLF.getCurrentPosition());
                    telemetry.addData("Position LB", motorLB.getCurrentPosition());
                    telemetry.addData("Position RF", motorRF.getCurrentPosition());
                    telemetry.addData("Position RB", motorRB.getCurrentPosition());

                }
                else{
                    state = States.TURN_TO_LINE1;
                    motorRB.setPower(0.0);
                    motorRF.setPower(0.0);
                    motorLB.setPower(0.0);
                    motorLF.setPower(0.0);

                }
                break;


            case TURN_TO_LINE1:
                colorSensorL.enableLed(true);
                colorSensorR.enableLed(true);
                if (R <= WhiteMaxValR && R >= WhiteMinValR)
                {
                    telemetry.addData("R", R);
                    telemetry.addData("Alpha", colorSensorR.alpha());

                    motorRB.setPower(0.0);
                    motorRF.setPower(0.0);
                    motorLB.setPower(0.0);
                    motorLF.setPower(0.0);
                    sleep(1000);
                    state = States.TURN_TO_LINE2;


                }
                else
                {

                    motorLB.setPower(0.25);
                    motorLF.setPower(0.25);
                    telemetry.addData("R", R);
                    telemetry.addData("Alpha", colorSensorR.alpha());


                }
                break;

            case TURN_TO_LINE2:
                telemetry.addData("R", R);
                telemetry.addData("Alpha", colorSensorR.alpha());

                if (L <= WhiteMaxValL && L >= WhiteMinValL)
                {
                    motorRB.setPower(0.0);
                    motorRF.setPower(0.0);
                    motorLB.setPower(0.0);
                    motorLF.setPower(0.0);
                    sleep(1000);
                    state = States.LINE_FOLOWING;

                }
                else
                {
                    motorLB.setPower(0.25);
                    motorLF.setPower(0.25);

                }
                break;

            case LINE_FOLOWING:
                        followCnt--;

                        L = readAvgHue(colorSensorL);
                        R = readAvgHue(colorSensorR);
                        telemetry.addData("L", L);
                        telemetry.addData("R", R);
                        //O = ODS.getRawLightDetected();
                        if (L >= WhiteMinValL && L <= WhiteMaxValL) {
                            motorLF.setPower(0.4);
                            motorLB.setPower(0.4);
                            motorRF.setPower(0.5);
                            motorRB.setPower(0.5);
                        }
                        else if (R >= WhiteMinValR && R <= WhiteMaxValR) {
                            motorLF.setPower(0.5);
                            motorLB.setPower(0.5);
                            motorRF.setPower(0.4);
                            motorRB.setPower(0.4);
                        }
                        else {
                            motorLF.setPower(0.4);
                            motorLB.setPower(0.4);
                            motorRF.setPower(0.4);
                            motorRB.setPower(0.4);
                        }

                if(followCnt == 0) {
                    motorLF.setPower(0.0);
                    motorLB.setPower(0.0);
                    motorRF.setPower(0.0);
                    motorRB.setPower(0.0);
                    state = States.PRESS_BUTTON;
                }
                break;

            case CAMERA:
                colorSensorL.enableLed(false);
                colorSensorR.enableLed(false);
                if (imageReady()) {
                    int redValueLeft = -76800;
                    int blueValueLeft = -76800;
                    int greenValueLeft = -76800;
                    int redValueRight = -76800;
                    int blueValueRight = -76800;
                    int greenValueRight = -76800;

                    Bitmap rgbImage;

                    //Put results to phone with red/blue/green
                    rgbImage = convertYuvImageToRgb(yuvImage, width, height, ds2);
                    for (int x = 0; x < 240; x++) {
                        for (int y = 0; y < 320; y++) {
                            rgbImage.setPixel(x, y, greatestColor(rgbImage.getPixel(x, y)));
                        }
                    }
                    SaveImage(rgbImage);


                    //Evaluating left side of screen/beacon
                    for (int x = 0; x < 120; x++) {
                        for (int y = 90; y < 230; y++) {
                            int pixelL = rgbImage.getPixel(x, y);
                            redValueLeft += red(pixelL);
                            blueValueLeft += blue(pixelL);
                            greenValueLeft += green(pixelL);
                        }
                    }

                    //Evaluating right side of screen/beacon
                    for (int a = 121; a < 240; a++) {
                        for (int b = 90; b < 230; b++) {
                            int pixelR = rgbImage.getPixel(a, b);
                            redValueRight += red(pixelR);
                            blueValueRight += blue(pixelR);
                            greenValueRight += green(pixelR);
                        }
                    }
                    redValueLeft = normalizePixels(redValueLeft);
                    blueValueLeft = normalizePixels(blueValueLeft);
                    greenValueLeft = normalizePixels(greenValueLeft);
                    redValueRight = normalizePixels(redValueRight);
                    blueValueRight = normalizePixels(blueValueRight);
                    greenValueRight = normalizePixels(greenValueRight);
                    int colorLeft = highestColor(redValueLeft, greenValueLeft, blueValueLeft);
                    int colorRight = highestColor(redValueRight, greenValueRight, blueValueRight);
                    String colorStringLeft = "";
                    String colorStringRight = "";
                    switch (colorLeft) {
                        case 0:
                            colorStringLeft = "RED";
                            break;
                        case 1:
                            colorStringLeft = "GREEN";
                            break;
                        case 2:
                            colorStringLeft = "BLUE";
                    }
                    switch (colorRight) {
                        case 0:
                            colorStringRight = "RED";
                            break;
                        case 1:
                            colorStringRight = "GREEN";
                            break;
                        case 2:
                            colorStringRight = "BLUE";
                    }

                    if (colorStringLeft == "BLUE") {
                        bBPP = 0;
                        //0 = Left
                        state = States.PRESS_BUTTON;
                        break;
                    }
                    else if (colorStringRight == "BLUE") {
                        bBPP = 1;
                        //1 = Right
                        state = States.PRESS_BUTTON;
                        break;
                    }
                    else {
                        sleep(1000);
                        break;
                    }
                }
            case PRESS_BUTTON:
                sleep(1000);
                /*if(bBPP == 1)
                {
                    bBP.setPosition(0.2);
                    //Position for Right Side of Beacon
                }
                else
                {
                    bBP.setPosition(0.8);

                    //Position for Left Side of Beacon
                }*/
                break;
        }
        //telemetry.update();
}
    public static void sleep(int amt) // In milliseconds
    {
        double a = System.currentTimeMillis();
        double b = System.currentTimeMillis();
        while ((b - a) <= amt) {
            b = System.currentTimeMillis();
        }
    }
    public float readAvgHue(ColorSensor colorSensor)
    {
        float averagedARGB = 0;
        float argbValToAverage = 0;
        boolean flag = true;
        for (int i = 2; i!= 10; ++i )
        {

            if (flag) {
                float arbg = colorSensor.argb() / 1000000;
                argbValToAverage = arbg + arbg;
            }
            flag = false;

            float arbg = colorSensor.argb() / 1000000;
            argbValToAverage = argbValToAverage + arbg;
        }

        averagedARGB = argbValToAverage / 10;



        return  averagedARGB;
    }

}





