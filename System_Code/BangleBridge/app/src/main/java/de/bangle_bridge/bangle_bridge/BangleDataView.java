package de.bangle_bridge.bangle_bridge;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import data.Model;

/**
 * elij
 *
 * @author Jorge
 * @version 1.5
 * @since 1.0
 */
public class BangleDataView extends AppCompatActivity implements BtListener {
    Model model = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangle_data_view);
        Intent intent = getIntent();
        model = (Model) intent.getSerializableExtra("Model");
    }

    private String deviceAddress;
    private BtService service;

    private TextView receiveText;
    private boolean pendingNewline = false;
    private String newline = TxtUtil.newline_crlf;


    private void receive(byte[] data) {//recievoing messages from device

        String msg = new String(data);//Get recieved adata from arguments to strin g
        if (newline.equals(TxtUtil.newline_crlf) && msg.length() > 0) {//Look fro end of the line
            //retorno de linea CR
            msg = msg.replace(TxtUtil.newline_crlf, TxtUtil.newline_lf);
            // CR + LF
            if (pendingNewline && msg.charAt(0) == '\n') {
                Editable edt = receiveText.getEditableText();
                if (edt != null && edt.length() > 1)
                    edt.replace(edt.length() - 2, edt.length(), "");
            }
            pendingNewline = msg.charAt(msg.length() - 1) == '\r';
        }
        receiveText.append(TxtUtil.toCaretString(msg, newline.length() != 0));

    }

    private void disconnect() {

        service.disconnect();
    }


    private void status(String str) {//Print Spawneable String on textBox
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    @Override
    public void onStop() {
        Log.d("TestDebugging", "LLAMADA onStop2");
        /*if(service != null && !getActivity().isChangingConfigurations())
            service.detach();*/
        super.onStop();
    }


    // BtListener Implemntation
    @Override
    public void onSerialConnect() {
        status("connected");
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }

    /**
     * Called when pointer capture is enabled or disabled for the current window.
     *
     * @param hasCapture True if the window has pointer capture.
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}