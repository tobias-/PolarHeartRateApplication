package org.marco45.polarheartmonitor;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class GoogleFitnessIntegration implements Observer {

    private final DataSource dataSource;
    private final GoogleApiClient googleApiClient;

    public GoogleFitnessIntegration(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.RECORDING_API)
                .build();
        googleApiClient.connect();
        dataSource = new DataSource.Builder()
                .setStreamName("PolarHeartBPM")
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_HEART_RATE_BPM)
                .setType(DataSource.TYPE_RAW)
                .build();
    }

    public void connect() {
        DataHandler.getInstance().addObserver(this);
        Fitness.RecordingApi.subscribe(googleApiClient, dataSource);
    }

    public void disconnect() {
        googleApiClient.disconnect();
        DataHandler.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Number && ((Number) data).intValue() != 0) {
            DataSet dataSet = DataSet.create(dataSource);
            DataPoint dataPoint = dataSet.createDataPoint().setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS).setFloatValues(((Number) data).floatValue());
            dataSet.add(dataPoint);
        }
    }
}
