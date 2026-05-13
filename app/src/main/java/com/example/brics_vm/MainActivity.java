package com.example.brics_vm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.example.brics_vm.ui.marketplace.MarketplaceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private Menu menu;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            updateMenuVisibility(destination.getId());
            // При смене страницы закрываем поиск
            if (searchView != null && !searchView.isIconified()) {
                searchView.setIconified(true);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint("Поиск по названию курса...");

        // Показываем строку поиска сразу при нажатии на лупу
        searchView.setOnSearchClickListener(v -> {
            // Скрываем название приложения при открытии поиска
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("");
            }
        });

        // При закрытии поиска возвращаем название
        searchView.setOnCloseListener(() -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("BRICS Courses");
            }
            // Очищаем поиск (показываем все курсы)
            applySearchToFragment("");
            return false;
        });

        // ПОИСК ПРИ КАЖДОМ ИЗМЕНЕНИИ ТЕКСТА (без кнопки "Поиск"!)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // При нажатии Enter тоже ищем
                applySearchToFragment(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Поиск при каждом изменении текста (в реальном времени)
                applySearchToFragment(newText);
                return true;
            }
        });

        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        if (logoutItem != null) {
            SpannableString spanString = new SpannableString(logoutItem.getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanString.length(), 0);
            logoutItem.setTitle(spanString);
        }

        updateMenuVisibility(R.id.navigation_marketplace);
        return true;
    }

    private void applySearchToFragment(String query) {
        // Находим MarketplaceFragment
        androidx.navigation.fragment.NavHostFragment navHostFragment =
                (androidx.navigation.fragment.NavHostFragment)
                        getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            List<androidx.fragment.app.Fragment> fragments =
                    navHostFragment.getChildFragmentManager().getFragments();

            if (!fragments.isEmpty() && fragments.get(0) instanceof MarketplaceFragment) {
                MarketplaceFragment fragment = (MarketplaceFragment) fragments.get(0);
                fragment.filterCourses(query);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_logout) {
            showLogoutConfirmationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMenuVisibility(int destinationId) {
        if (menu == null) return;

        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem logoutItem = menu.findItem(R.id.action_logout);

        if (searchItem != null) {
            searchItem.setVisible(destinationId == R.id.navigation_marketplace);
        }
        if (logoutItem != null) {
            logoutItem.setVisible(destinationId == R.id.navigation_profile);
        }

        // Если уходим с маркетплейса, закрываем поиск
        if (destinationId != R.id.navigation_marketplace && searchView != null) {
            searchView.setIconified(true);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("BRICS Courses");
            }
        }
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Выход из аккаунта")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да", (dialog, which) -> logout())
                .setNegativeButton("Нет", null)
                .show();
    }

    private void logout() {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Проверяем, нужно ли перейти на маркетплейс
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("navigate_to_marketplace", false)) {
            // Убираем флаг, чтобы не сработало повторно
            intent.removeExtra("navigate_to_marketplace");

            // Переключаемся на вкладку маркетплейса
            BottomNavigationView navView = findViewById(R.id.nav_view);
            if (navView != null) {
                navView.setSelectedItemId(R.id.navigation_marketplace);
            }
        }
    }

}