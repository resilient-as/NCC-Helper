package com.example.ncc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ncc.Connection.ConnectionClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {
    EditText editTextTextPersonName,editTextTextPassword;
    Button cologin, anologin, cadetlogin;
    TextView cadetregistration;
    String deviceID;

    Connection con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* View is a class where all widgets are defined.
         *  R.id.myName specifies a view whose ID name is called myName.
         *  R is a class that contains the ID's of all the views.
         *  findViewById is the method that finds the View by the ID is given. So findViewById(R.id.myName) finds the View with 'myName'.
         * */
        editTextTextPersonName = (EditText) findViewById(R.id.editTextTextPersonName);
        editTextTextPassword = (EditText) findViewById(R.id.editTextTextPassword);
        cologin = (Button) findViewById(R.id.cologin);
        anologin = (Button)findViewById(R.id.anologin);
        cadetregistration = (TextView) findViewById(R.id.cadetregistration);
        cadetlogin = (Button) findViewById(R.id.cadetlogin);

        //Method to get Device Id
        deviceID= Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);

        //OnClickListener for CO Login
        cologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginActivity.checkCOLogin().execute("");
            }
        });
        //OnClickListener for ANO Login
        anologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginActivity.checkANOlogin().execute("");
            }
        });
        //OnClickListener for Cadet Login
        cadetlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginActivity.checkCADETLogin().execute("");
            }
        });
        //OnClickListener for Cadet Registration
        cadetregistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(LoginActivity.this, CadetRegistration.class);
                startActivity(intent1);
                finish();
            }
        });
    }

    public class checkCADETLogin extends AsyncTask<String, String, String> {
        String z = null;
        Boolean isSuccess = false;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected String doInBackground(String... strings) {
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(), ConnectionClass.db.toString(),ConnectionClass.ip.toString());
            if(con == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"Check Internet Connection",Toast.LENGTH_LONG).show();
                    }
                });
                z = "On Internet Connection";
            }
            else {
                try {
                    String sql = "SELECT * FROM CADET WHERE [AADHAR CARD NUMBER] = '" + editTextTextPersonName.getText() + "' AND [CONTACT NUMBER] = '" + editTextTextPassword.getText() + "' AND [ANDROID ID] = '" +deviceID+ "' ";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    if (rs.next()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                            }
                        });
                        z = "Success";

                        Intent intent = new Intent(LoginActivity.this, ANOActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Check username or password", Toast.LENGTH_LONG).show();
                            }
                        });

                        editTextTextPersonName.setText("");
                        editTextTextPassword.setText("");
                    }
                } catch (Exception e) {
                    isSuccess = false;
                    Log.e("SQL Error : ", e.getMessage());
                }
            }
            return z;
        }

    }

    public class checkANOlogin extends AsyncTask<String, String, String> {
        String z = null;
        Boolean isSuccess = false;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected String doInBackground(String... strings) {
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(), ConnectionClass.db.toString(),ConnectionClass.ip.toString());
            if(con == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"Check Internet Connection",Toast.LENGTH_LONG).show();
                    }
                });
                z = "On Internet Connection";
            }
            else {
                try {
                    String sql = "SELECT * FROM ANO WHERE USERNAME = '" + editTextTextPersonName.getText() + "' AND PASSWORD = '" + editTextTextPassword.getText() + "' ";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    if (rs.next()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                            }
                        });
                        z = "Success";

                        Intent intent = new Intent(LoginActivity.this, ANOActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Check username or password", Toast.LENGTH_LONG).show();
                            }
                        });

                        editTextTextPersonName.setText("");
                        editTextTextPassword.setText("");
                    }
                } catch (Exception e) {
                    isSuccess = false;
                    Log.e("SQL Error : ", e.getMessage());
                }
            }
            return z;
        }

    }

    public class checkCOLogin extends AsyncTask<String, String, String> {
        String z = null;
        Boolean isSuccess = false;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected String doInBackground(String... strings) {
            con = connectionClass(ConnectionClass.un.toString(),ConnectionClass.pass.toString(), ConnectionClass.db.toString(),ConnectionClass.ip.toString());
            if(con == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"Check Internet Connection",Toast.LENGTH_LONG).show();
                    }
                });
                z = "On Internet Connection";
            }
            else {
                try {
                    String sql = "SELECT * FROM CO WHERE USERNAME = '" + editTextTextPersonName.getText() + "' AND PASSWORD = '" + editTextTextPassword.getText() + "' ";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    if (rs.next()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                            }
                        });
                        z = "Success";

                        Intent intent = new Intent(LoginActivity.this, COActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Check username or password", Toast.LENGTH_LONG).show();
                            }
                        });

                        editTextTextPersonName.setText("");
                        editTextTextPassword.setText("");
                    }
                } catch (Exception e) {
                    isSuccess = false;
                    Log.e("SQL Error : ", e.getMessage());
                }
            }
            return z;
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
}