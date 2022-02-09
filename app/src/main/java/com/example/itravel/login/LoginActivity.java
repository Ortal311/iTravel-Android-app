package com.example.itravel.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.itravel.R;

public class LoginActivity extends AppCompatActivity {
    NavController navCtl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NavHost navHost = (NavHost)getSupportFragmentManager().findFragmentById(R.id.login_navhost);
        navCtl = navHost.getNavController();

        NavigationUI.setupActionBarWithNavController(this,navCtl);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!super.onOptionsItemSelected(item)) {
            switch ((item.getItemId())) {
                case android.R.id.home: //back button on the action bar
                    navCtl.navigateUp();
                    break;
            }
        } else
            return true;
        return false;
    }
}
