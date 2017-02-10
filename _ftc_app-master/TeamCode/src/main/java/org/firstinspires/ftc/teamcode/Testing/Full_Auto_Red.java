package org.firstinspires.ftc.teamcode.Testing;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import for_camera_opmodes.OpModeCamera;

/**
 * Created by RoboticsUser on 1/26/2017.
 */
@Autonomous (name = "Full_Auto_Red1", group = "")
public class Full_Auto_Red extends OpModeCamera {
    DcMotor motorL;
    DcMotor motorLF;
    DcMotor motorR;
    DcMotor motorRF;
    Servo bBP;
    ColorSensor colorSensorL;
    ColorSensor colorSensorR;
    OpticalDistanceSensor ODS;
    DcMotor launcherWheel;
    Servo popper;
    DcMotor collector;

    String beaconColorLeft = "ERROR";
    String beaconColorRight = "ERROR";
    String[] beaconColors = new String[2];
    double[] positionRobot = new double[20];
    int center = 160;
    double theta = 112358;
    double topX = 132134;
    int ds1 = 1;
    double leftBoundary = 0;
    double rightBoundary = 0;
    int x = 0;
    int count = 0;
    int rev = 0;
    boolean test = true;
    boolean test1 = true;
    boolean testloop = true;
    boolean test3 = true;
    double constant = 0;
    int lastE = 0;
    int encoderCount;
    int cps = 0;
    long a2 = 0;
    long c2 = 0;
    long c = 0;
    long a = 0;
    double popperUp = 0.99;
    double popperDown = 0.8;
    int avg = 0;
    int p = 5;
    //p-Previous was 20
    int t = 100;
    //t-Previous was 10

    double launcherPower = 0;

    enum States {ReadBeacon, DriveToWhiteLine, PositionRobot, ShortForward, TurnToWhiteLine, TurnPastWhiteLine, ForwardToBeacon, StopRobot, SHOOT, SHOOT2}

    States state;

    public void init() {
        motorL = hardwareMap.dcMotor.get("motorL");
        motorLF = hardwareMap.dcMotor.get("motorLF");
        motorR = hardwareMap.dcMotor.get("motorR");
        motorRF = hardwareMap.dcMotor.get("motorRF");
        bBP = hardwareMap.servo.get("bBP");
        colorSensorL = hardwareMap.colorSensor.get("colorSensorL");
        colorSensorR = hardwareMap.colorSensor.get("colorSensorR");
        ODS = hardwareMap.opticalDistanceSensor.get("ODS");
        launcherWheel = hardwareMap.dcMotor.get("launcherWheel");
        popper = hardwareMap.servo.get("popper");
        collector = hardwareMap.dcMotor.get("collector");

        colorSensorR.setI2cAddress(I2cAddr.create7bit(0x1e));
        colorSensorL.setI2cAddress(I2cAddr.create7bit(0x26));
        colorSensorL.enableLed(true);
        colorSensorR.enableLed(true);

        setCameraDownsampling(2);
        super.init();

        state = States.DriveToWhiteLine;
    }

    public void loop() {
        telemetry.addData("State", state);

        switch (state) {
            case DriveToWhiteLine:
                if ((colorSensorR.argb() / 1000000) < 75) {
                    motorLF.setPower(-.25);
                    motorL.setPower(-.25);
                    motorRF.setPower(.25);
                    motorR.setPower(.25);
                    telemetry.addData("LeftPower", -.25);
                    telemetry.addData("RightPower", .25);
                } else {
                    motorL.setPower(0.0);
                    motorLF.setPower(0.0);
                    motorR.setPower(0.0);
                    motorRF.setPower(0.0);
                    motorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    motorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    state = States.ShortForward;
                }
                break;

            case ShortForward:
                if ((motorR.getCurrentPosition() > -10)) {
                    motorLF.setPower(-.25);
                    motorL.setPower(-.25);
                    motorRF.setPower(.25);
                    motorR.setPower(.25);

                } else {
                    motorL.setPower(0.0);
                    motorLF.setPower(0.0);
                    motorR.setPower(0.0);
                    motorRF.setPower(0.0);
                    motorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    motorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    state = States.TurnToWhiteLine;
                }
                break;

            case TurnToWhiteLine:
                if ((colorSensorR.argb() / 1000000 < 75)) {
                    motorLF.setPower(-.25);
                    motorL.setPower(-.25);
                    motorRF.setPower(-.25);
                    motorR.setPower(-.25);
                } else {
                    motorL.setPower(0.0);
                    motorLF.setPower(0.0);
                    motorR.setPower(0.0);
                    motorRF.setPower(0.0);
                    motorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    motorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    state = States.PositionRobot;
                }
                break;
            case TurnPastWhiteLine:
                if ((colorSensorL.argb() / 1000000 > 75)) {
                    motorLF.setPower(-.25);
                    motorL.setPower(-.25);
                    motorRF.setPower(-.25);
                    motorR.setPower(-.25);
                } else {
                    motorL.setPower(0.0);
                    motorLF.setPower(0.0);
                    motorR.setPower(0.0);
                    motorRF.setPower(0.0);
                    motorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    motorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    state = States.PositionRobot;
                }
                break;

            case PositionRobot:
                sleep(100);
                motorL.setPower(0.0);
                motorLF.setPower(0.0);
                motorR.setPower(0.0);
                motorRF.setPower(0.0);
                sleep(100);
                Bitmap rgbImage;
                rgbImage = convertYuvImageToRgb(yuvImage, width, height, 1);
                positionRobot = positionRobot(rgbImage);
                theta = positionRobot[0];
                topX = positionRobot[1];
                telemetry.addData("Theta", theta);
                telemetry.addData("Pivoting", topX - center);
                /*for (int x = 0; x < 480; x++) {
                    for (int y = 0; y < 640; y++) {

                        if (y == positionRobot[11]) {
                            rgbImage.setPixel(x, y, Color.rgb(255, 0, 0));
                        }
                        if (y == positionRobot[12]) {
                            rgbImage.setPixel(x, y, Color.rgb(0, 0, 255));
                        }
                        if (x == positionRobot[5]) {
                            rgbImage.setPixel(x, y, Color.rgb(255, 0, 0));
                        }
                        if (x == positionRobot[6]) {
                            rgbImage.setPixel(x, y, Color.rgb(0, 0, 255));
                        }
                    }
                }
                SaveImage(rgbImage);*/
                if ((topX - center) < -40) {
                    telemetry.addData("Pivot Fast", "Left!");
                    motorL.setPower(-0.50);
                    motorLF.setPower(-0.50);
                    motorR.setPower(-0.50);
                    motorRF.setPower(-0.50);
                    break;
                } else if ((topX - center) > 40) {
                    telemetry.addData("Pivot Fast", "Right!");
                    motorL.setPower(0.50);
                    motorLF.setPower(0.50);
                    motorR.setPower(0.50);
                    motorRF.setPower(0.50);
                    break;
                }
                else if ((topX - center) < -10) {
                    telemetry.addData("Pivot Slow", "Left!");
                    motorL.setPower(-0.30);
                    motorLF.setPower(-0.30);
                    motorR.setPower(-0.30);
                    motorRF.setPower(-0.30);
                    break;
                } else if ((topX - center) > 10) {
                    telemetry.addData("Pivot Slow", "Right!");
                    motorL.setPower(0.30);
                    motorLF.setPower(0.30);
                    motorR.setPower(0.30);
                    motorRF.setPower(0.30);
                    break;
                } else {
                    telemetry.addData("Pivot", (topX - center));
                    motorL.setPower(0.0);
                    motorLF.setPower(0.0);
                    motorR.setPower(0.0);
                    motorRF.setPower(0.0);
                    state = States.ReadBeacon;
                    break;
                }
                    /*
                    telemetry.addData("Theta", positionRobot[0]);
                    telemetry.addData("TopX", positionRobot[1]);
                    telemetry.addData("Top Y", positionRobot[2]);
                    telemetry.addData("Bottom X", positionRobot[3]);
                    telemetry.addData("Bottom Y", positionRobot[4]);
                    break;*/

            case ReadBeacon:
                beaconColors = readBeacon();
                beaconColorLeft = beaconColors[0];
                beaconColorRight = beaconColors[1];
                telemetry.addData("Left Color", beaconColorLeft);
                telemetry.addData("Right Color", beaconColorRight);
                if(beaconColorLeft.equals(beaconColorRight))
                {
                    sleep(3000);
                    telemetry.addData("Same Color", "Why?");
                    break;
                }
                if(beaconColorLeft.equals("RED"))
                {
                    bBP.setPosition(0.55);
                    state = States.ForwardToBeacon;
                    break;
                }
                if(beaconColorLeft.equals("BLUE"))
                {
                    bBP.setPosition(0.9);
                    state = States.ForwardToBeacon;
                    break;
                }
                break;

            case ForwardToBeacon:
                if(ODS.getRawLightDetected() < .15)
                {
                    telemetry.addData("ODS_Raw_Light", ODS.getRawLightDetected());
                    motorLF.setPower(-.15);
                    motorL.setPower(-.15);
                    motorRF.setPower(.15);
                    motorR.setPower(.15);
                    break;
                }
                else
                {
                    motorL.setPower(0.0);
                    motorLF.setPower(0.0);
                    motorR.setPower(0.0);
                    motorRF.setPower(0.0);
                    state = States.SHOOT;
                    break;
                }
            case SHOOT:
            {
                if(test)
                {
                    //c = System.currentTimeMillis();
                    test = false;
                    launcherPower = -.47;
                    launcherWheel.setPower(launcherPower);
                    //avg = launcherWheel.getCurrentPosition();
                }

                /*
                a = System.currentTimeMillis();
                if((a - c) < 2000)
                {
                    break;
                }

                else
                {
                */
                if(count < 2)
                {
                    if (avg < -2150 && avg > -2250)
                    {
                        count++;
                        state = States.SHOOT2;
                    }
                    else
                    {
                        if(test3)
                        {
                            c2 = System.currentTimeMillis();
                            test3 = false;
                        }
                        a2 = System.currentTimeMillis();
                        if((a2 - c2) < t)
                        {
                            break;
                        }
                        else
                        {
                            if (avg < -2250) {
                                launcherPower = launcherPower + .002;
                            }
                            if (avg > -2150) {
                                launcherPower = launcherPower - .002;
                            }
                            launcherWheel.setPower(launcherPower);
                            test3 = true;
                        }

                        break;
                    }

                    test = true;
                }
                else
                {
                    launcherWheel.setPower(0);
                    state = States.StopRobot;
                    break;
                }
                break;
                //}
            }

            case SHOOT2:
                if(test1)
                {
                    c = System.currentTimeMillis();
                    test1 = false;
                }
                popper.setPosition(popperUp);
                a = System.currentTimeMillis();
                if((a - c) < 1000)
                {
                    break;
                }
                else
                {
                    popper.setPosition(popperDown);
                    //launcherWheel.setPower(0.00);
                    state = States.SHOOT;
                    test = true;
                    test1 = true;
                }
                break;

            case StopRobot:
                motorL.setPower(0.0);
                motorLF.setPower(0.0);
                motorR.setPower(0.0);
                motorRF.setPower(0.0);
        }
    }

    private String[] readBeacon() {
        String[] endCondition = new String[2];
        if (imageReady()) {
            int redValueLeft = -76800;
            int blueValueLeft = -76800;
            int greenValueLeft = -76800;
            int redValueRight = -76800;
            int blueValueRight = -76800;
            int greenValueRight = -76800;

            Bitmap rgbImage;

            rgbImage = convertYuvImageToRgb(yuvImage, width, height, ds1);

            if((topX - 100 < 0 ))
            {
                leftBoundary = 0;
            }
            else
            {
                leftBoundary = topX - 100;
            }

            if((topX + 100 > 480))
            {
                rightBoundary = 480;
            }
            else
            {
                rightBoundary = topX + 100;
            }
            //Evaluating left side of screen/beacon
            for (int x = (int)leftBoundary; x < (topX - 20); x++) {
                for (int y = 120; y < 270; y++) {
                    int pixelL = rgbImage.getPixel(x, y);
                    redValueLeft += red(pixelL);
                    blueValueLeft += blue(pixelL);
                    greenValueLeft += green(pixelL);
                }
            }
            //Evaluating right side of screen/beacon
            for (int a = (int)(topX + 20); a < rightBoundary; a++) {
                for (int b = 120; b < 270; b++) {
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
            for (int x = 0; x < 480; x++) {
                for (int y = 0; y < 640; y++) {

                    if (x == leftBoundary) {
                        rgbImage.setPixel(x, y, Color.rgb(255, 0, 0));
                    }
                    if (x == rightBoundary) {
                        rgbImage.setPixel(x, y, Color.rgb(255, 0, 0));
                    }
                    if (x == topX - 20) {
                        rgbImage.setPixel(x, y, Color.rgb(0, 0, 255));
                    }
                    if (x == topX + 20) {
                        rgbImage.setPixel(x, y, Color.rgb(0, 0, 255));
                    }
                    if (y == 120 || y == 270) {
                        rgbImage.setPixel(x, y, Color.rgb(0, 255, 0));
                    }
                }
            }
            SaveImage(rgbImage);
            endCondition[0] = colorStringLeft;
            endCondition[1] = colorStringRight;

        }
        return endCondition;
    }

    private double[] positionRobot(Bitmap rgbImage) {
        int topX;
        int topY;
        int topBeginX = 0;
        int topEndX = 0;
        boolean line = true;
        int bottomX;
        int bottomY;
        int bottomBeginX = 0;
        int bottomEndX = 0;
        double theta;
        boolean bottomFlag = false;
        boolean topFlag = false;
        int white = 120;
        double[] returnArray = new double[15];

        int pixelWidth = 480;
        int pixelHeight = 640;

        boolean[] bottomPixels;
        bottomPixels = new boolean[pixelWidth];

        boolean[] topPixels;
        topPixels = new boolean[pixelWidth];
        int pixelPlaceholder;

        //Get bottom line center
        for (int x = 0; x < pixelWidth; x++) {
            pixelPlaceholder = rgbImage.getPixel(x, (pixelHeight - 3));
            if ((Color.red(pixelPlaceholder) > white) && (Color.blue(pixelPlaceholder) > white) && (Color.green(pixelPlaceholder) > white)) {
                bottomPixels[x] = true;
            } else {
                bottomPixels[x] = false;
            }
        }

        for (int x = 0; x < pixelWidth; x++) {
            if (!bottomFlag) {
                if (bottomPixels[x]) {
                    bottomBeginX = x;
                    bottomFlag = true;
                }
            } else {
                if (!bottomPixels[x]) {
                    bottomEndX = x;
                    x = pixelWidth;
                }
            }
        }
        //Average the center of the line
        bottomX = (bottomBeginX + ((bottomEndX - bottomBeginX) / 2));
        bottomY = (pixelHeight - 3);
        //Point of topLine
        int y;
        for (y = (pixelHeight - 3); line; y--) {
            //Create and analyze an array for each y-value
            for (int x = 0; x < pixelWidth; x++) {
                pixelPlaceholder = rgbImage.getPixel(x, y);
                if ((Color.red(pixelPlaceholder) > white) && (Color.blue(pixelPlaceholder) > white) && (Color.green(pixelPlaceholder) > white)) {
                    topPixels[x] = true;
                } else {
                    topPixels[x] = false;
                }
            }
            line = false;
            for (int x = 0; x < pixelWidth; x++) {
                if (topPixels[x]) {
                    line = true;
                }
            }
        }
        if (y > 635) {
            y = 630;
        }
        for (int x = 0; x < pixelWidth; x++) {
            pixelPlaceholder = rgbImage.getPixel(x, (y + 5));
            if ((Color.red(pixelPlaceholder) > white) && (Color.blue(pixelPlaceholder) > white) && (Color.green(pixelPlaceholder) > white)) {
                topPixels[x] = true;
            } else {
                topPixels[x] = false;
            }
        }

        for (int x = 0; x < pixelWidth; x++) {
            if (!topFlag) {
                if (topPixels[x]) {
                    topBeginX = x;
                    topFlag = true;
                }
            } else {
                if (!topPixels[x]) {
                    topEndX = x;
                    x = pixelWidth;
                }
            }
        }
        //Average the center of the line
        topX = (topBeginX + ((topEndX - topBeginX) / 2));
        topY = y;

        //Analyze the image and find the two points

        double opposite = center - topX;
        double adjacent = topY - bottomY;
        theta = Math.atan(opposite / adjacent);
        theta = Math.toDegrees(theta);
        returnArray[0] = theta;
        returnArray[1] = topX;
        returnArray[2] = topY;
        returnArray[3] = bottomX;
        returnArray[4] = bottomY;

        returnArray[5] = bottomX;
        returnArray[6] = topX;
        returnArray[11] = bottomY;
        returnArray[12] = topY;

        return returnArray;
    }

    private static void sleep(int time) // In milliseconds
    {
        double constant = System.currentTimeMillis();
        double current = System.currentTimeMillis();
        while ((current - constant) <= time) {
            current = System.currentTimeMillis();
        }
    }
}

