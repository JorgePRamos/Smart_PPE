package de.bangle_bridge.bangle_bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;

import data.Measurement;
import data.Model;

public class InputFragment extends Fragment implements ServiceConnection, SerialListener {

    private enum Connected { False, Pending, True }

    private String deviceAddress;
    private SerialService service;

    private TextView receiveText;
    public  TextView textTest;
    public Button testButton;
    private TextView sendText;
    private TextUtil.HexWatcher hexWatcher;

    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;
    public Model model = new Model();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");//get arguments from intent bundle
        Log.d("TestDebugging", "LLAMADA onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d("TestDebugging", "LLAMADA onDestroy");
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.d("TestDebugging", "LLAMADA onStart");
        super.onStart();
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        Log.d("TestDebugging", "LLAMADA onStop");
        /*if(service != null && !getActivity().isChangingConfigurations())
            service.detach();*/
        super.onStop();
    }


    @Override
    public void onAttach(@NonNull Activity activity) { //executesd wehn fragments gets added to activity
        Log.d("TestDebugging", "LLAMADA onAttach");
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {//executesd wehn fragments gets delets from activity
        Log.d("TestDebugging", "LLAMADA onDetach");
        try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        Log.d("TestDebugging", "LLAMADA onResume");
        super.onResume();
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("TestDebugging", "LLAMADA onServiceDisconnected");
        service = null;
    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Terminal fragment
        View view = inflater.inflate(R.layout.fragment_input, container, false);//Get XML of terminal
        //Recieved text box
        receiveText = view.findViewById(R.id.receive_text); // Text box for recived text
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); //Color of text
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance()); // set text scroll
        //Test text box
        testButton = view.findViewById(R.id.elbutton);

        testButton.setOnClickListener(v -> showTestData(view));//On click send text to text box



        return view;
    }
    public void showTestData(View v){
        int cont = 0;
        TextView textBox = (TextView) v.findViewById(R.id.showText);
        Log.d("TestWatcher", "Me clickean");
        for(Measurement m : model.measurements){
            cont ++;
            Log.d("TestWatcher", "Loopeo");
            if(m != null){
                Log.d("TestWatcher", m.toString());
                textBox.setText(m.toString());


            }

        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) { //Change drop Down menu to terminal options
        inflater.inflate(R.menu.menu_terminal, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// if-Else tree for options menu
        int id = item.getItemId();//Get item Id
        if (id == R.id.clear) { //Thrash can option to clear
            receiveText.setText("");
            return true;
        } else if (id == R.id.newline) { //New Line option
            String[] newlineNames = getResources().getStringArray(R.array.newline_names);
            String[] newlineValues = getResources().getStringArray(R.array.newline_values);
            int pos = java.util.Arrays.asList(newlineValues).indexOf(newline);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Newline");
            builder.setSingleChoiceItems(newlineNames, pos, (dialog, item1) -> {
                newline = newlineValues[item1];
                dialog.dismiss();
            });
            builder.create().show();
            return true;
        } else if (id == R.id.TESTPAGE) {//Enable // Disable hex mode
            Log.d("TestDebugging", "--->INTETN");
            Intent myIntent = new Intent(this.getContext(), BangleDataView.class);
            myIntent.putExtra("Model",model);
            startActivity(myIntent);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void connect() {
        try {
            //Get bluethood device to connect
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            status("connecting...");//Sapawn Status String
            connected = Connected.Pending;//set status as pending With ENun
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
    }

    String onBuild = "";
    public  void jsonWatcher(String in){

        Log.d("JsonNull","IN --> "+in);
        if (in.contains("#")){
            onBuild += in;
            Log.d("JsonWatchAdd", onBuild);
            Measurement out  =  Measurement.fromJsonToObj(onBuild);
            Log.d("JsonNull",out.toString());
            model.measurements.add(out);
            onBuild = "";
        }else {

            onBuild += in;
            Log.d("JsonWatch", onBuild);
        }




    }

    private void receive(byte[] data) {//recievoing messages from device

        if(hexEnabled) {//Check hex
            receiveText.append(TextUtil.toHexString(data) + '\n');
        } else {
            String msg = new String(data);//Get recieved adata from arguments to strin g
            if(newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {//Look fro end of the line
                // don't show CR as ^M if directly before LF
                msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
                // special handling if CR and LF come in separate fragments
                if (pendingNewline && msg.charAt(0) == '\n') {
                    Editable edt = receiveText.getEditableText();
                    if (edt != null && edt.length() > 1)
                        edt.replace(edt.length() - 2, edt.length(), "");
                }
                pendingNewline = msg.charAt(msg.length() - 1) == '\r';
            }
            String appendable ="";
            try{
                appendable = (String) TextUtil.toCaretString(msg, newline.length() != 0);
            }catch(  ClassCastException e){
                e.printStackTrace();
            }
            receiveText.append(TextUtil.toCaretString(msg, newline.length() != 0));
            jsonWatcher(appendable);
            Log.d("TestDebugging","\n###################\n");



        }
    }


    private void status(String str) {//Print Spawneable String on textBox
        SpannableStringBuilder spn = new SpannableStringBuilder(str+'\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    //Serial Listener mehtod implementation
    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        Log.d("TestDebugging","DATA Recivida");
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }

}
