package com.dam.tfg.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.dam.tfg.R;

public class SensorActivity extends AppCompatActivity implements
        SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private static final float SHAKE_THRESHOLD = 1.1f;
    private static final int SHAKE_WAIT_TIME_MS = 250;
    private static final float ROTATION_THRESHOLD = 2.0f;
    private static final int ROTATION_WAIT_TIME_MS = 100;
    TextView xgiro;
    TextView ygiro;
    TextView zgiro;
    TextView xsacu;
    TextView ysacu;
    TextView zsacu;
    TextView mov;
    private long mShakeTime = 0;
    private long mRotationTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sacudidayrotacion);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        xgiro = (TextView) findViewById(R.id.xgiro);
        ygiro = (TextView) findViewById(R.id.ygiro);
        zgiro = (TextView) findViewById(R.id.zgiro);
        xsacu = (TextView) findViewById(R.id.xsacudida);
        ysacu = (TextView) findViewById(R.id.ysacudida);
        zsacu = (TextView) findViewById(R.id.zsacudida);
        mov = (TextView) findViewById(R.id.movimiento);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xsacu.setText("x=" + Float.toString(event.values[0]));
            ysacu.setText("y=" + Float.toString(event.values[1]));
            zsacu.setText("z=" + Float.toString(event.values[2]));
            detectShake(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            xgiro.setText("x=" + Float.toString(event.values[0]));
            ygiro.setText("y=" + Float.toString(event.values[1]));
            zgiro.setText("z=" + Float.toString(event.values[2]));
            detectRotation(event);
        }
    }

    private void detectRotation(SensorEvent event) {
        long now = System.currentTimeMillis();
        if ((now - mRotationTime) > ROTATION_WAIT_TIME_MS) {
            mRotationTime = now;
            // Change background color if rate of rotation around any
            // axis and in any direction exceeds threshold;
            // otherwise, reset the color
            if (Math.abs(event.values[0]) > ROTATION_THRESHOLD ||
                    Math.abs(event.values[1]) > ROTATION_THRESHOLD ||
                    Math.abs(event.values[2]) > ROTATION_THRESHOLD) {
                mov.setText("Rotación detectada");
            }
        }
    }

    private void detectShake(SensorEvent event) {
        long now = System.currentTimeMillis();
        if ((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
            mShakeTime = now;
            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;
            // gForce will be close to 1 when there is no movement
            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);
            // Change background color if gForce exceeds threshold;
            // otherwise, reset the color
            if (gForce > SHAKE_THRESHOLD) {
                mov.setText("Sacudida detectada");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
