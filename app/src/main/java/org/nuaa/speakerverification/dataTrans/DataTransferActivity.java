package org.nuaa.speakerverification.dataTrans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.nuaa.speakerverification.R;
import org.nuaa.speakerverification.dataTrans.Receiver.RecordTask;
import org.nuaa.speakerverification.dataTrans.Sender.BufferSoundTask;
import org.nuaa.speakerverification.dataTrans.Setting.SettingsActivity;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

public class DataTransferActivity extends AppCompatActivity implements CallbackSendRec {

    private ProgressBar sendingBar;

    private boolean isSending = false;

    private boolean isListening = false;

    private boolean isReceiving = false;

    private BufferSoundTask sendTask = null;

    private RecordTask listenTask = null;

    private String sendTextData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);

        sendingBar = findViewById(R.id.progressBar);
    }


    //call when data is sending
    public void sendData(View view) {

        if (isListening) {
            stopListening();
            if (listenTask != null) {
                listenTask.setWorkFalse();
            }
        }

        if (!isSending) {
            sendTextData = ((TextView) findViewById(R.id.editData)).getText().toString();
            if (!sendTextData.isEmpty() && !sendTextData.equals("")) {
                isSending = true;
                sendingBar.setVisibility(View.VISIBLE);
                sendTask = new BufferSoundTask();
                sendTask.setProgressBar(sendingBar);
                sendTask.setCallbackSR(this);
                ((Button) view).setText(R.string.stop);

                try {
                    byte[] byteText = sendTextData.getBytes("UTF-8");
                    sendTask.setBuffer(byteText);
                    Integer[] tempArr = getSettingsArguments();
                    sendTask.execute(tempArr);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        } else {
            //
            if(sendTask!=null){
                sendTask.setWorkFalse();
            }
            stopSending();
        }
    }


    //listen button click
    public void listenData(View view) {

        if(isSending){
            stopSending();
            if(sendTask!=null){
                sendTask.setWorkFalse();
            }
        }

        if(!isListening) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            } else {
                listen();
            }
        }

        else{
            if(listenTask!=null){
                listenTask.setWorkFalse();
            }
            stopListening();
        }

    }

    //Called to start listening task and update GUI to listening
    private void listen(){
        isListening=true;
        ((Button)findViewById(R.id.listenButton)).setText(R.string.stop);
        Integer[] tempArr=getSettingsArguments();
        listenTask=new RecordTask();
        listenTask.setCallbackRet(this);
        listenTask.execute(tempArr);
    }

    //Called to reset view and flag to initial state from sending state
    private void stopSending(){
        ((Button) findViewById(R.id.sendButton)).setText(R.string.send);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sendingBar.setProgress(1, true);
        }
        else{
            sendingBar.setProgress(1);
        }
        sendingBar.setVisibility(View.GONE);
        isSending=false;
    }

    //Called to reset view and flag to initial state from listening state
    private void stopListening(){
        if(isReceiving){
            ((TextView) findViewById(R.id.receivingState)).setText(R.string.noReceived);
            isReceiving=false;
        }
        ((Button) findViewById(R.id.listenButton)).setText(R.string.listen);
        isListening=false;
    }


    @Override
    public void actionDone(int srFlag, String message) {
        //If its sending task and activity is still in sending mode
        if(CallbackSendRec.SEND_ACTION==srFlag && isSending){
            //Update GUI to initial state
            stopSending();

        } else {
            //If its receiving task and activity is still in receiving mode
            if(CallbackSendRec.RECEIVE_ACTION==srFlag && isListening){
                //Update GUI to initial state
                stopListening();
                //If received message exists put it in database and show it on view
                if(!message.equals("")){
                    ((TextView) findViewById(R.id.receivingState)).setText(message);

                }
            }
        }

    }

    @Override
    public void receivingSomething() {
        ((TextView) findViewById(R.id.receivingState)).setText("Receiving Data...");
        isReceiving = true;
    }

    //get parameters from settings preferences
    private Integer[] getSettingsArguments() {
        Integer[] tempArr = new Integer[5];
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        tempArr[0] = Integer.parseInt(preferences.getString(SettingsActivity.KEY_START_FREQUENCY,
                SettingsActivity.DEF_START_FREQUENCY));
        tempArr[1] = Integer.parseInt(preferences.getString(SettingsActivity.KEY_END_FREQUENCY,
                SettingsActivity.DEF_END_FREQUENCY));
        tempArr[2] = Integer.parseInt(preferences.getString(SettingsActivity.KEY_BIT_PER_TONE,
                SettingsActivity.DEF_BIT_PER_TONE));

        if (preferences.getBoolean(SettingsActivity.KEY_ERROR_DETECTION,
                SettingsActivity.DEF_ERROR_DETECTION)) {
            tempArr[3] = 1;
        } else {
            tempArr[3] = 0;
        }
        tempArr[4] = Integer.parseInt(preferences.getString(SettingsActivity.KEY_ERROR_BYTE_NUM,
                SettingsActivity.DEF_ERROR_BYTE_NUM));
        return tempArr;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                //If user granted permission on mic, continue with listening
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listen();
                }
                break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (listenTask != null) {
            stopListening();
            listenTask.setWorkFalse();
        }

        if (sendTask != null) {
            stopSending();
            sendTask.setWorkFalse();
        }
    }
}
