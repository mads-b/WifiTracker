package net.svamp.wifitracker;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CompassProcessor implements SensorEventListener {
    /* sensor data */
    SensorManager m_sensorManager;
    float []m_lastMagFields = new float[3];
    float []m_lastAccels = new float[3];
    private float[] m_rotationMatrix = new float[16];
    private float[] m_orientation = new float[4];

    Filter m_filter = new Filter();



    public CompassProcessor(Activity ac) {
        m_sensorManager = (SensorManager) ac.getSystemService(Context.SENSOR_SERVICE);
    }

    public void registerListeners() {
        m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregisterListeners() {
        m_sensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accel(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mag(event);
        }
    }

    private void accel(SensorEvent event) {
        System.arraycopy(event.values, 0, m_lastAccels, 0, 3);
    }

    private void mag(SensorEvent event) {
        System.arraycopy(event.values, 0, m_lastMagFields, 0, 3);
        computeOrientation();
    }

    /**
     * Moving average filter
     */
    private class Filter {
        static final int AVERAGE_BUFFER = 20;
        float[] m_arr = new float[AVERAGE_BUFFER];
        int m_idx = 0;

        public void append(float val) {
            m_arr[m_idx] = val;
            m_idx++;
            if (m_idx == AVERAGE_BUFFER)
                m_idx = 0;
        }
        public float getFiltered() {
            float sum = 0;

            for (float x: m_arr) {
                sum += x;
            }
            return sum / AVERAGE_BUFFER;
        }

    }

    public void computeOrientation() {
        if (SensorManager.getRotationMatrix(m_rotationMatrix, null, m_lastMagFields, m_lastAccels)) {
            SensorManager.getOrientation(m_rotationMatrix, m_orientation);
            /* yaw, rotation around z axis */
            float yaw = m_orientation[0];
            m_filter.append(yaw);
        }
    }

    public double getOrientation() {
        return m_filter.getFiltered();
    }

} 
