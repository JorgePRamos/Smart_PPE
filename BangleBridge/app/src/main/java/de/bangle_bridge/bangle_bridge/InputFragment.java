package de.bangle_bridge.bangle_bridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bson.Document;

import java.lang.reflect.Type;

import java.util.HashMap;

import data.Measurement;
import data.Model;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

import static android.content.Context.MODE_PRIVATE;

public class InputFragment extends Fragment implements ServiceConnection, SerialListener {
    public Model model = null;
    private Thread thread;
    String appId = "banglebridge-hrhlb";
    //Activity elements
    private TextView receiveText;
    private LineChart hrmChart;
    private TextView hrmMonitor;
    private Button syncWithMongoBT;

    //BTLE
    private String deviceAddress;
    private SerialService service;

    //CONST
    private enum StateOfConnection {False, Pending, True}

    private StateOfConnection connected = StateOfConnection.False;
    private String newline = TextUtil.newline_crlf;

    //State Booleans
    private boolean plotChart = true;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean pendingNewline = false;


    //Activity Life Cycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loadData();
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");//get arguments from intent bundle

        Realm.init(getContext());
        Credentials apiCredential = Credentials.apiKey("IMsL2CGxqW3Ks424o1fxiKuLMZkDPrHlr9actpkZdDuAstBMsMf7RXDb29TjTtR8");
        Credentials credentials = Credentials.emailPassword("someMail@gmail.com","somePass");
        App app = new App(new AppConfiguration.Builder(appId).build());
        app.loginAsync(apiCredential, new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if(result.isSuccess()){
                  User usr = app.currentUser();

                    Log.v("QUICKSTART", "Successfully authenticated anonymously.");
                    MongoClient mongoClient = usr.getMongoClient("mongodb-atlas");
                    MongoDatabase mongoDatabase =
                            mongoClient.getDatabase("MeasurementsDB");
                    MongoCollection<Document> mess =
                            mongoDatabase.getCollection("Measurements");

                    Document toy = new Document("userid", usr.getId()) .append("ages", new Document("min", 5));
                    mess.insertOne(toy).getAsync(task -> {
                        if (task.isSuccess()) {
                            Log.v("Insert", "successfully inserted a document with id: " + task.get().getInsertedId());
                        } else {
                            Log.e("Insert", "failed to insert documents with: " + task.getError().getErrorMessage());
                        }
                    });

                }
            }
        });

        model = new Model();
        /*
        Credentials credentials = Credentials.anonymous();
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully authenticated anonymously.");

                // interact with realm using your user object here
            } else {
                Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
            }
        });


        User usr = app.currentUser();

        Log.d("MONGO","CURRENT USER --->"+usr.toString()+"   "+usr.getAccessToken());

        MongoClient mongoClient = usr.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase =
                mongoClient.getDatabase("MeasurementsDB");
        MongoCollection<Document> mess =
                mongoDatabase.getCollection("Measurements");

        Document toy = new Document("name", "yoyo") .append("ages", new Document("min", 5));
        mess.insertOne(toy).getAsync(task -> {
            if (task.isSuccess()) {
                Log.v("Insert", "successfully inserted a document with id: " + task.get().getInsertedId());
            } else {
                Log.e("Insert", "failed to insert documents with: " + task.getError().getErrorMessage());
            }
        });
        */




        Log.d("TestDebugging", "Llamada onCreate");
    }

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
        syncWithMongoBT = view.findViewById(R.id.button2);
        //Test text box
        //testButton = view.findViewById(R.id.elbutton);
        syncWithMongoBT.setOnClickListener(v -> syncMongo(view));
        //testButton.setOnClickListener(v -> showTestData(view));//On click send text to text box

        createChard(view);


        return view;
    }

    @Override
    public void onStart() {
        Log.d("TestDebugging", "Llamada onStart");
        super.onStart();

        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onDestroy() {
        Log.d("TestDebugging", "Llamada onDestroy");
        saveData();
        if (connected != StateOfConnection.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    public void onStop() {
        Log.d("TestDebugging", "Llamada onStop");
        /*if(service != null && !getActivity().isChangingConfigurations())
            service.detach();*/
        super.onStop();
    }

    @Override
    public void onAttach(@NonNull Activity activity) { //executesd wehn fragments gets added to activity
        Log.d("TestDebugging", "Llamada onAttach");
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {//executesd wehn fragments gets delets from activity
        Log.d("TestDebugging", "Llamada onDetach");
        try {
            getActivity().unbindService(this);
        } catch (Exception ignored) {
        }
        super.onDetach();
    }

    @Override
    public void onResume() {
        Log.d("TestDebugging", "Llamada onResume");
        loadData();//posible errror
        super.onResume();
        if (initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if (initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("TestDebugging", "Llamada onServiceDisconnected");
        service = null;
    }


    //Save/Load data from Shared preferences
    public void syncMongo(View v){
        Log.d("TimeDebug","SUBIENDO A MONGO : )");
        model.mongoUpMap();



    }
    public void saveData() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(model.measurements);
        editor.putString("messurementsList", json);
        editor.apply();

    }

    public void loadData() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("messurementsList", null);
        Type type = new TypeToken<HashMap<String, Measurement>>() {
        }.getType();
        model.measurements = gson.fromJson(json, type);
        if (model.measurements == null) {
            model.measurements = new HashMap<String, Measurement>();
        }

    }

    //Chard Creation
    public void createChard(View v) {

        hrmChart = (LineChart) v.findViewById(R.id.lineChart);

        // enable description text
        hrmChart.getDescription().setEnabled(true);

        // enable touch gestures
        hrmChart.setTouchEnabled(true);

        // enable scaling and dragging
        hrmChart.setDragEnabled(true);
        hrmChart.setScaleEnabled(true);
        hrmChart.setDrawGridBackground(true);

        // if disabled, scaling can be done on x- and y-axis separately
        hrmChart.setPinchZoom(true);

        // set an alternative background color
        hrmChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        hrmChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = hrmChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = hrmChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = hrmChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        //leftAxis.setAxisMaximum(10f);
        //leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = hrmChart.getAxisRight();
        rightAxis.setEnabled(false);

        hrmChart.getAxisLeft().setDrawGridLines(false);
        hrmChart.getXAxis().setDrawGridLines(false);
        hrmChart.setDrawBorders(false);

        feedMultiple();

    }

    private void addEntry(Float hrmChar) {

        LineData data = hrmChart.getData();

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
            hrmChart.notifyDataSetChanged();

            // limit the number of visible entries
            hrmChart.setVisibleXRangeMaximum(150);
            // hrmChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            hrmChart.moveViewToX(data.getEntryCount());

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

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    plotChart = true;
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

    //Receive and send to Bangle
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void receive(byte[] data) {//recievoing messages from device

        if (hexEnabled) {//Check hex
            receiveText.append(TextUtil.toHexString(data) + '\n');
        } else {
            String msg = new String(data);//Get recieved adata from arguments to strin g
            if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {//Look fro end of the line
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
            String appendable = "";
            try {
                appendable = (String) TextUtil.toCaretString(msg, newline.length() != 0);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            receiveText.append(TextUtil.toCaretString(msg, newline.length() != 0));
            jsonWatcher(appendable);
            Log.d("TestDebugging", "\n###################\n");


        }
    }

    private void send(String str) {
        if (connected != StateOfConnection.True) {//If not conncetted throw textBox alert

            return;
        }
        try {
            String msg;
            byte[] data;
            if (hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                msg = str;
                data = (str + newline).getBytes();//Build String with str + \n
            }
            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);//append spawn string to text box
            service.write(data);//Write my built string
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    //Top Right Menu
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
            myIntent.putExtra("Model", model);
            startActivity(myIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //BT Connection Handler
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
    public void jsonWatcher(String in) {
        Log.d("SavingJson", "Nº --> " + model.measurements.size());
        Log.d("JsonNull", "IN --> " + in);
        if (in.contains("#")) {
            onBuild += in;
            Log.d("JsonWatchAdd", onBuild);
            Measurement out = Measurement.fromJsonToObj(onBuild);
            // Log.d("JsonNull",out.toString());
            if (out != null) {
                String output = "Scanning...";
                Log.d("hrmdebugg", String.valueOf(out.getHrm()));
                output.valueOf(out.getHrm());
                hrmMonitor.setText(String.valueOf(out.getHrm()));
                if (plotChart) {
                    float algo = (float) out.getHrm();
                    addEntry(algo);//cambiar
                    plotChart = false;
                }
                model.measurements.put((out.getTime()), out);
            }
            onBuild = "";

        } else {

            onBuild += in;
            Log.d("JsonWatch", onBuild);
        }


    }

    private void status(String str) {//Print Spawneable String on textBox
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }


    //Serial Listener methods implementation
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSerialRead(byte[] data) {
        Log.d("TestDebugging", "DATA Recivida");
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }

}
