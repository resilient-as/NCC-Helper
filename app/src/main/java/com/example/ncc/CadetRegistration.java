package com.example.ncc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ncc.Connection.ConnectionClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CadetRegistration extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener{

    private static final int REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int SELECT_PICTURE=200;

    Spinner statespinner,cityspinner,collegespinner,unitspinner,gender;
    EditText firstname, surname, fathername, mothername, aadharnumber,
            nationality, contactnumber, pincode, postaladdress;
    TextView enrollment_year, status, dateofbirth;
    Connection con;
    Statement stmt;
    Button registerbtn, dobbutton, InsertImageButton;
    ImageView cadetsphoto;
    String DeviceID, image, SelectedState, SelectedCity, SelectedCollege;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadet_registration);

        /*View is a class where all the widgets are defined.
        R.id.myName specifies a view whose ID name is called myName
        R is a Class that contains the ID's of all the Views*/

//Method to get Android device unique id
        DeviceID= Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);

//EditText Initialization
        firstname=findViewById(R.id.FirstName);
        surname=findViewById(R.id.SurName);
        fathername=findViewById(R.id.FatherName);
        mothername=findViewById(R.id.MotherName);
        aadharnumber=findViewById(R.id.AadharNumber);
        nationality=findViewById(R.id.Nationality);
        contactnumber=findViewById(R.id.ContactNumber);
        pincode=findViewById(R.id.PinCode);
        postaladdress=findViewById(R.id.PostalAddress);
//Image View Initialization
        cadetsphoto=findViewById(R.id.cadetsphoto);
//Button Initialization
        registerbtn=findViewById(R.id.registerbtn);
        dobbutton=findViewById(R.id.DOBbtn);
        InsertImageButton=findViewById(R.id.InsertImageButton);
//Spinner Initialization
        statespinner=(Spinner) findViewById(R.id.statespinner);
        cityspinner=(Spinner) findViewById(R.id.cityspinner);
        collegespinner=(Spinner) findViewById(R.id.collegespinner);
        unitspinner=(Spinner) findViewById(R.id.unitspinner);
        gender=(Spinner) findViewById(R.id.gender);
//Text View Initialization
        dateofbirth=findViewById(R.id.DateOfBirth);
        status=findViewById(R.id.status);
        enrollment_year=findViewById(R.id.Enrollment_Year);

        //Setting Enrollment Year text View
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String currentDateandTime = sdf.format(new Date());
        enrollment_year.setText(currentDateandTime);

//Action on Clicking Insert Photo Button
        findViewById(R.id.InsertImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE
                )!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            CadetRegistration.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else{
                    selectedImage();
                }
            }
        });
//Date Picker Dialog
        findViewById(R.id.DOBbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
//Adding Element into Gender Spinner
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("M");
        arrayList.add("F");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(arrayAdapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Gender = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), "Selected: " + Gender,Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//Calling state spinner function
        fillingstatespinner();

        statespinner.setOnItemSelectedListener(this);
        cityspinner.setOnItemSelectedListener(this);
        collegespinner.setOnItemSelectedListener(this);

        //Registration button on Click listener
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(
                        TextUtils.isEmpty(firstname.getText())||
                        TextUtils.isEmpty(surname.getText())||
                        TextUtils.isEmpty(fathername.getText())||
                        TextUtils.isEmpty(mothername.getText())||
                        TextUtils.isEmpty(aadharnumber.getText())||
                        TextUtils.isEmpty(nationality.getText())||
                        TextUtils.isEmpty(contactnumber.getText())||
                        TextUtils.isEmpty(pincode.getText())||
                        TextUtils.isEmpty(postaladdress.getText())
                ){
                    status.setText("Text Field is Empty");
                }
                else {
                    new CadetRegistration.registercadet().execute("");
                }
            }
        });

    }
//Function definition
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = + dayOfMonth + "/" + (month+1) + "/" + year;
        dateofbirth.setText(date);
    }
// on registerbtn click, register user
    public class registercadet extends AsyncTask<String, String , String> {

        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            status.setText("Sending Data to Database");
        }

        @Override
        protected void onPostExecute(String s) {
            status.setText("Registration Successful");
            firstname.setText("");
            surname.setText("");
            fathername.setText("");
            mothername.setText("");
            statespinner.getSelectedItem();
            cityspinner.getSelectedItem();
            collegespinner.getSelectedItem();
            unitspinner.getSelectedItem();
            aadharnumber.setText("");
            nationality.setText("");
            dateofbirth.setText("");
            contactnumber.setText("");
            postaladdress.setText("");
            pincode.setText("");
            gender.getSelectedItem();
        }
        @Override
        protected String doInBackground(String... strings) {
            try{
                con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(),ConnectionClass.db.toString(),ConnectionClass.ip.toString());
                if(con == null){
                    z = "Check Your Internet Connection";
                }
                else{
                    String sql = "INSERT INTO CADET ([FIRST NAME],[SUR NAME],[FATHER'S NAME],[MOTHER'S NAME],STATE,CITY,COLLEGE,UNIT," +
                            "[AADHAR CARD NUMBER],NATIONALITY,[DATE OF BIRTH],\n" +
                            "[CONTACT NUMBER],ADDRESS,[PIN CODE],GENDER,YEAR,PHOTO,[ANDROID ID])  " +
                            "VALUES('"+firstname.getText()+"','"+surname.getText()+"','"+fathername.getText()+"'," +
                            "'"+mothername.getText()+"','"+statespinner.getSelectedItem()+"','"+cityspinner.getSelectedItem()+"" +
                            "','"+collegespinner.getSelectedItem()+"','"+unitspinner.getSelectedItem()+"','"+aadharnumber.getText()+"" +
                            "','"+nationality.getText()+"','"+dateofbirth.getText()+"','"+contactnumber.getText()+"','"+postaladdress.getText()+"" +
                            "','"+pincode.getText()+"','"+gender.getSelectedItem()+"','"+enrollment_year.getText()+"','"+image+"','"+DeviceID+"')";
                    stmt = con.createStatement();
                    stmt.executeUpdate(sql);
                }

            }catch (Exception e){
                isSuccess = false;
                z = e.getMessage();
            }

            return z;
        }
    }
    private void selectedImage(){
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                selectedImage();
            }else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode==RESULT_OK){
            if(requestCode==SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                if(selectedImageUri!=null){
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        cadetsphoto.setImageBitmap(bitmap);
                        //converting image into byteArray Format
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        image = Base64.encodeToString(bytes , Base64.DEFAULT);

                    }catch(Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    public void fillingstatespinner(){
        try{
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(), ConnectionClass.db.toString(),ConnectionClass.ip.toString());
            String query = "select distinct StateName from StateCity";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String> data = new ArrayList<>();
            while(rs.next()){
                String statename = rs.getString("StateName");
                data.add(statename);
            }
//Create an Array Adapter to fill our spinner
            ArrayAdapter array =  new ArrayAdapter(this,android.R.layout.simple_list_item_1,data);
            statespinner.setAdapter(array);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void fillingcityspinner(){
        try{
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(), ConnectionClass.db.toString(),ConnectionClass.ip.toString());
            String query = "select CityName from StateCity where StateName='"+SelectedState+"'";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String> dataofcity = new ArrayList();
            dataofcity.clear();
            while(rs.next()){
                String cityname = rs.getString("CityName");
                dataofcity.add(cityname);
            }
//Create an Array Adapter to fill our spinner
            ArrayAdapter arrayofcity =  new ArrayAdapter(this,android.R.layout.simple_list_item_1,dataofcity);
            cityspinner.setAdapter(arrayofcity);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void fillingcollegespinner(){
        try{
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(), ConnectionClass.db.toString(),ConnectionClass.ip.toString());
            String query = "select distinct CollegeName from SchoolCollegesUnit where CityName='"+SelectedCity+"'";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String> dataofcollege = new ArrayList();
            dataofcollege.clear();
            while(rs.next()){
                String collegename = rs.getString("CollegeName");
                dataofcollege.add(collegename);
            }
//Create an Array Adapter to fill our spinner
            ArrayAdapter arrayofcollege =  new ArrayAdapter(this,android.R.layout.simple_list_item_1,dataofcollege);
            collegespinner.setAdapter(arrayofcollege);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void fillingunitspinner(){
        try{
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(), ConnectionClass.db.toString(),ConnectionClass.ip.toString());
            String query = "select Unit from SchoolCollegesUnit where CollegeName='"+SelectedCollege+"'";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String> dataofunit = new ArrayList();
            dataofunit.clear();
            while(rs.next()){
                String unitname = rs.getString("Unit");
                dataofunit.add(unitname);
            }
//Create an Array Adapter to fill our spinner
            ArrayAdapter arrayofunit =  new ArrayAdapter(this,android.R.layout.simple_list_item_1,dataofunit);
            unitspinner.setAdapter(arrayofunit);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionClass(String user, String password, String database, String server){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + server+"/" + database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(connectionURL);
        }catch (Exception e){
            Log.e("SQL Connection Error : ", e.getMessage());
        }

        return connection;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.statespinner){
            SelectedState = parent.getSelectedItem().toString();
            fillingcityspinner();
        }
        if(parent.getId()==R.id.cityspinner){
            SelectedCity = parent.getSelectedItem().toString();
            fillingcollegespinner();
        }
        if(parent.getId()==R.id.collegespinner){
            SelectedCollege = parent.getSelectedItem().toString();
            fillingunitspinner();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}