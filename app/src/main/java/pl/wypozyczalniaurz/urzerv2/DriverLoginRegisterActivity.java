package pl.wypozyczalniaurz.urzerv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginRegisterActivity extends AppCompatActivity {

    private Button DriverLoginButton;
    private Button DriverRegisterButton;
    private TextView DriverRegisterLink;
    private TextView DriverStatus;
    private EditText EmailDriver;
    private EditText PasswordDriver;
    private ProgressDialog loadingBar;
    private DatabaseReference DriverDatabaseRef;
    private String onlineDriverID;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_register);

        mAuth = FirebaseAuth.getInstance();
        DriverLoginButton = (Button) findViewById(R.id.driver_login_btn);
        DriverRegisterButton = (Button) findViewById(R.id.driver_register_btn);
        DriverRegisterLink = (TextView) findViewById(R.id.driver_register_link);
        DriverStatus = (TextView) findViewById(R.id.driver_status);
        EmailDriver = (EditText) findViewById(R.id.email_driver);
        PasswordDriver = (EditText) findViewById(R.id.password_driver);
        loadingBar = new ProgressDialog(this); //instancja loading baru

        DriverRegisterButton.setVisibility(View.INVISIBLE); //Ustawiam niewidzialność Register Buttona
        DriverRegisterButton.setEnabled(false);

        //Ustawiam onClick na "Nie jesteś zarejestrowany?"; znika Login Button i jeden TextView; pojawia się Register Button
        DriverRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DriverLoginButton.setVisibility(View.INVISIBLE);
                DriverRegisterLink.setVisibility(View.INVISIBLE);
                DriverStatus.setText("Rejestracja Kierowcy"); //Zmieniam setText jednego z TextView

                DriverRegisterButton.setVisibility(View.VISIBLE);
                DriverRegisterButton.setEnabled(true);
            }
        });
        //Ustawiam onClick na buttonie Zarejestruj
        DriverRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();

                RegisterDriver(email, password); //Wywołuję metodę
            }
        });

        DriverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();

                SignInDriver(email, password);
            }
        });
    }
    private void SignInDriver(String email, String password)
    {
        if(TextUtils.isEmpty(email))//Warunek jeżeli nie wpisał maila
        {
            Toast.makeText(DriverLoginRegisterActivity.this, "Wpisz e-mail...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))//Warunek jeżeli nie wpisał maila
        {
            Toast.makeText(DriverLoginRegisterActivity.this, "Wpisz hasło...", Toast.LENGTH_SHORT).show();
        }
        else //Jeżeli nie puste - logowanie
        {
            loadingBar.setTitle("Logowanie Kierowcy");
            loadingBar.setMessage("Proszę czekać, logowanie trwa...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful()) //Jeżeli zostało authenticated
                            {
                                Intent driverIntent = new Intent(DriverLoginRegisterActivity.this, DriverMapActivity.class);
                                startActivity(driverIntent);

                                Toast.makeText(DriverLoginRegisterActivity.this, "Logowanie pomyślne", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(DriverLoginRegisterActivity.this, "Logowanie niepomyślne", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
}
    private void RegisterDriver(String email, String password)
    {
        if(TextUtils.isEmpty(email))//Warunek jeżeli nie wpisał maila
        {
            Toast.makeText(DriverLoginRegisterActivity.this, "Wpisz e-mail...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))//Warunek jeżeli nie wpisał maila
        {
            Toast.makeText(DriverLoginRegisterActivity.this, "Wpisz hasło...", Toast.LENGTH_SHORT).show();
        }
        else //Jeżeli nie puste - rejestracja
        {
            loadingBar.setTitle("Rejestracja Kierowcy");
            loadingBar.setMessage("Proszę czekać, rejestracja trwa...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful()) //Jeżeli zostało authenticated
                            {
                                onlineDriverID = mAuth.getCurrentUser().getUid();
                                DriverDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Kierowcy").child(onlineDriverID);
                                DriverDatabaseRef.setValue(true);

                                Intent driverIntent = new Intent(DriverLoginRegisterActivity.this, DriverMapActivity.class);
                                startActivity(driverIntent);

                                Toast.makeText(DriverLoginRegisterActivity.this, "Kierowca zarejestrowany pomyślnie", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(DriverLoginRegisterActivity.this, "Rejestracja niepomyślna, spróbuj ponownie", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
}
