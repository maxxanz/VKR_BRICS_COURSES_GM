package com.example.brics_vm;

import android.content.Intent; // ДОБАВЬТЕ ЭТОТ ИМПОРТ
import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.example.brics_vm.ui.auth.LoginFragment;
import com.example.brics_vm.ui.auth.RegisterFragment;

public class AuthActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Проверяем, не вошел ли уже пользователь
        boolean isLoggedIn = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            // Если уже вошел, сразу переходим в MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_auth);

        tabLayout = findViewById(R.id.tab_layout);
        container = findViewById(R.id.auth_container);

        // По умолчанию показываем экран входа
        loadFragment(new LoginFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadFragment(new LoginFragment());
                } else {
                    loadFragment(new RegisterFragment());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.auth_container, fragment)
                .commit();
    }

    // В AuthActivity.java добавьте этот метод, если его нет
    public void switchToLoginTab() {
        if (tabLayout != null) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if (tab != null) {
                tab.select();
            }
        }
    }
}