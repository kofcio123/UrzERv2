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

public class CustomerLoginRegisterActivity extends AppCompatActivity {

    //Inicjalizacja buttonów
    private Button CustomerLoginButton;
    private Button CustomerRegisterButton;
    private TextView CustomerRegisterLink;
    private TextView CustomerStatus;
    private EditText EmailCustomer;
    private EditText PasswordCustomer;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference CustomerDatabaseRef;
    private String onlineCustomerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_register);

        mAuth = FirebaseAuth.getInstance();

        //Instancje
        CustomerLoginButton = (Button) findViewById(R.id.customer_login_btn);
        CustomerRegisterButton = (Button) findViewById(R.id.customer_register_btn);
        CustomerRegisterLink = (TextView) findViewById(R.id.register_customer_link);
        CustomerStatus = (TextView) findViewById(R.id.customer_status);
        EmailCustomer = (EditText) findViewById(R.id.email_customer);
        PasswordCustomer = (EditText) findViewById(R.id.password_customer);
        loadingBar = new ProgressDialog(this); //instancja loading baru

        CustomerRegisterButton.setVisibility(View.INVISIBLE); //Ustawiam niewidzialność Register Buttona
        CustomerRegisterButton.setEnabled(false);

        //Ustawiam onClick na "Nie jesteś zarejestrowany?"; znika Login Button i jeden TextView; pojawia się Register Button
        CustomerRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CustomerLoginButton.setVisibility(View.INVISIBLE);
                CustomerRegisterLink.setVisibility(View.INVISIBLE);
                CustomerStatus.setText("Rejestracja Klienta"); //Zmieniam setText jednego z TextView

                CustomerRegisterButton.setVisibility(View.VISIBLE);
                CustomerRegisterButton.setEnabled(true);
            }
        });

        CustomerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = EmailCustomer.getText().toString();
                String password = PasswordCustomer.getText().toString();

                RegisterCustomer(email, password); //Wywołuję metodę
            }
        });

        CustomerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = EmailCustomer.getText().toString();
                String password = PasswordCustomer.getText().toString();

                SignInCustomer(email, password);
            }
        });
    }

    private void SignInCustomer(String email, String password)
    {
        if(TextUtils.isEmpty(email))//Warunek jeżeli nie wpisał maila
        {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Wpisz e-mail...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))//Warunek jeżeli nie wpisał maila
        {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Wpisz hasło...", Toast.LENGTH_SHORT).show();
        }
        else //Jeżeli nie puste - logowanie
        {
            loadingBar.setTitle("Logowanie Klienta");
            loadingBar.setMessage("Proszę czekać, logowanie trwa...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful()) //Jeżeli zostało authenticated
                            {
                                Intent customerIntent = new Intent(CustomerLoginRegisterActivity.this, CustomerMapActivity.class);
                                startActivity(customerIntent);

                                Toast.makeText(CustomerLoginRegisterActivity.this, "Logowanie pomyślne", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Logowanie niepomyślne", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void RegisterCustomer(String email, String password)
    {
        if(TextUtils.isEmpty(email))//Warunek jeżeli nie wpisał maila
        {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Wpisz e-mail...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))//Warunek jeżeli nie wpisał maila
        {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Wpisz hasło...", Toast.LENGTH_SHORT).show();
        }
        else //Jeżeli nie puste - rejestracja
        {
            loadingBar.setTitle("Rejestracja Klienta");
            loadingBar.setMessage("Proszę czekać, rejestracja trwa...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful()) //Jeżeli zostało authenticated
                            {
                                onlineCustomerID = mAuth.getCurrentUser().getUid();
                                CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Klienci").child(onlineCustomerID);

                                CustomerDatabaseRef.setValue(true);

                                Intent driverIntent = new Intent(CustomerLoginRegisterActivity.this, CustomerMapActivity.class);
                                startActivity(driverIntent);

                                Toast.makeText(CustomerLoginRegisterActivity.this, "Klient zarejestrowany pomyślnie", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Rejestracja niepomyślna, spróbuj ponownie", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
}
