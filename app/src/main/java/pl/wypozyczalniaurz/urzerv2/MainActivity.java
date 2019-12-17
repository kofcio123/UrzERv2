package pl.wypozyczalniaurz.urzerv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button WelcomeDriverButton;
    private Button WelcomeCustomerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WelcomeCustomerButton = (Button) findViewById(R.id.welcome_customer_btn);
        WelcomeDriverButton = (Button) findViewById(R.id.welcome_driver_btn);

        //Aktywność pod przyciskiem "Jestem Klientem"
        WelcomeCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginRegisterCustomerIntent = new Intent(MainActivity.this, CustomerLoginRegisterActivity.class);
                startActivity(LoginRegisterCustomerIntent);
            }
        });
        //Aktywność pod przyciskiem "Jestem Kierowcą"
        WelcomeDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginRegisterDriverIntent = new Intent(MainActivity.this, DriverLoginRegisterActivity.class);
                startActivity(LoginRegisterDriverIntent);
            }
        });
    }
}

