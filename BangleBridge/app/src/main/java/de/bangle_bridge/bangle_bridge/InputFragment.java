package de.bangle_bridge.bangle_bridge;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softmoore.android.graphlib.Function;
import com.softmoore.android.graphlib.Graph;
import com.softmoore.android.graphlib.GraphView;
import com.softmoore.android.graphlib.Label;
import com.softmoore.android.graphlib.Point;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.graphics.Color;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import data.Measurement;
import data.Model;

import static android.content.Context.MODE_PRIVATE;

public class InputFragment extends Fragment implements ServiceConnection, SerialListener {

    

    private String deviceAddress;
    private SerialService service;

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private enum StateOfConnection { False, Pending, True }

    private TextView receiveText;
    public  TextView textTest;
    public Button testButton;
    private TextView sendText;
    private TextUtil.HexWatcher hexWatcher;
    private TextView hrmMonitor;
    private StateOfConnection connected = StateOfConnection.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;
    public Model model = new Model();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");//get arguments from intent bundle
        Log.d("TestDebugging", "LLAMADA onCreate");


    }

    @Override
    public void onDestroy() {
        Log.d("TestDebugging", "LLAMADA onDestroy");
        saveData();
        if (connected != StateOfConnection.False)
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
    public void onPause() {
        super.onPause();
        saveData();
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
        loadData();//posible errror
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

    public void saveData(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(model.measurements);
        editor.putString("messurementsList", json);
        editor.apply();


    }
    public void loadData(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("messurementsList", null);
        Type type = new TypeToken<HashMap<String,Measurement>>() {}.getType();
        model.measurements = gson.fromJson(json, type);
        if (model.measurements == null) {
            model.measurements = new HashMap<String,Measurement> ();
        }

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
        //eceiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); //Color of text
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance()); // set text scroll
        hrmMonitor = (TextView) view.findViewById(R.id.hrmdisplay);
        hrmMonitor.setText("0");
        //Test text box
        //testButton = view.findViewById(R.id.elbutton);

        //testButton.setOnClickListener(v -> showTestData(view));//On click send text to text box

        createChard(view);



        return view;
    }

    public void createChard(View v){


        mChart = (LineChart) v.findViewById(R.id.lineChart);

        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        //leftAxis.setAxisMaximum(10f);
        //leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();

    }

    private void addEntry(Float hrmChar) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), hrmChar), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    /*
    public void showGraph(View v) {
        Graph graph = new Graph.Builder()
                .build();
        GraphView graphView = v.findViewById(R.id.graph_view);
        graphView.setGraph(graph);

    }

    public void showTestData(View v){
        int cont = 0;
        TextView textBox = (TextView) v.findViewById(R.id.);
        Log.d("TestWatcher", "Me clickean");
        for(Measurement m : model.measurements){
            cont ++;
            Log.d("TestWatcher", "Loopeo");
            if(m != null){
                Log.d("TestWatcher", m.toString());
                textBox.setText(m.toString());


            }

        }

    }*/

    private void send(String str) {
        if(connected != StateOfConnection.True) {//If not conncetted throw textBox alert

            return;
        }
        try {
            String msg;
            byte[] data;
            if(hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                msg = str;
                data = (str + newline).getBytes();//Build String with str + \n
            }
            SpannableStringBuilder spn = new SpannableStringBuilder(msg+'\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);//append spawn string to text box
            service.write(data);//Write my built string
        } catch (Exception e) {
            onSerialIoError(e);
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
            connected = StateOfConnection.Pending;//set status as pending With ENun
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = StateOfConnection.False;
        service.disconnect();
    }

    String onBuild = "";
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void jsonWatcher(String in){
        Log.d("SavingJson","Nº --> "+model.measurements.size());
        Log.d("JsonNull","IN --> "+in);
        if (in.contains("#")){
            onBuild += in;
            Log.d("JsonWatchAdd", onBuild);
            Measurement out  =  Measurement.fromJsonToObj(onBuild);
           // Log.d("JsonNull",out.toString());
            if (out  != null) {
                String output = "Scanning...";
                Log.d("hrmdebugg", String.valueOf(out.getHrm()));
                output.valueOf(out.getHrm());
                hrmMonitor.setText( String.valueOf(out.getHrm()));
                if(plotData){
                    float algo = (float) out.getHrm();
                    addEntry(algo);//cambiar
                    plotData = false;
                }
                model.measurements.put((out.getTime().toString()),out);
            }
                onBuild = "";

        }else {

            onBuild += in;
            Log.d("JsonWatch", onBuild);
        }




    }


    @RequiresApi(api = Build.VERSION_CODES.O)
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
        connected = StateOfConnection.True;
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
