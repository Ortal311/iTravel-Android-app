package com.example.itravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    NavController navCtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NavHost navHost = (NavHost)getSupportFragmentManager().findFragmentById(R.id.main_navhost);
        navCtl = navHost.getNavController();

        NavigationUI.setupActionBarWithNavController(this,navCtl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(! super.onOptionsItemSelected(item)){
            //TODO: check if this menu is global for all the pages in the app, if not check class 6 - 02:00:00

            switch ((item.getItemId())){
                case R.id.menu_addpost:
                    navCtl.navigate(R.id.action_global_postAddFragment);
                    break;
                case R.id.menu_profile:
                    //TODO: add navigation to profile
                    break;
                case R.id.menu_homepage:
                    navCtl.navigate(R.id.action_global_homePageFragment);
                    break;
                case R.id.menu_signout:
                    //TODO: signout
                    break;
               case  android.R.id.home: //back button on the action bar
                     navCtl.navigateUp();
                     break;

            }
        }else
        {
            return true;
        }
        return false;
    }

}