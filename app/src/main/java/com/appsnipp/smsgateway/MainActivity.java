package com.appsnipp.smsgateway;

import static com.appsnipp.smsgateway.ui.utils.MyUtilsApp.checkInternetIsConnected;
import static com.appsnipp.smsgateway.ui.utils.MyUtilsApp.checkNetworkIsConnected;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.appsnipp.smsgateway.data.CallLogLocal;
import com.appsnipp.smsgateway.data.Sms;
import com.appsnipp.smsgateway.databinding.ActivityMainBinding;
import com.appsnipp.smsgateway.layanan.PhoneCallListener;
import com.appsnipp.smsgateway.layanan.SmsListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    NavHostFragment navHostFragment;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenter.start(getApplication(), "0f23c541-ea83-43df-ac09-3ae1ba0735b1",
                Analytics.class, Crashes.class);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);
        setAppTheme();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setupNavigation();
        checkNetworkIsConnected(this, callback);
    }

    private void setAppTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);

    }

    private void setupNavigation() {
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (navHostFragment != null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
        }
    }


    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private final ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            if(checkInternetIsConnected()) {
                List<Sms> smsList = ObjectBox.get().boxFor(Sms.class).getAll();
                SharedPreferences sp = getApplicationContext().getSharedPreferences("pref", 0);
                for (Sms sms : smsList) {
                    SmsListener.sendPOST(sp.getString("urlPost", null), sms, "local", getApplicationContext());
                }

                List<CallLogLocal> listCallLog = ObjectBox.get().boxFor(CallLogLocal.class).getAll();
                for (CallLogLocal callLog : listCallLog) {
                    PhoneCallListener.saveData(getApplicationContext(), callLog.callNumber, callLog.direction, callLog.callDate, callLog.callDuration, callLog, "all");
                }
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
        }
    };

}
