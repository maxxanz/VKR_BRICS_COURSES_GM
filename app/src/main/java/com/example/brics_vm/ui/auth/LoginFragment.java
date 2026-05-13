package com.example.brics_vm.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.brics_vm.MainActivity;
import com.example.brics_vm.R;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private static final String TAG = "LOGIN_DEBUG";

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private ProgressBar progressBar;
    private NstuApi nstuApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailInput = view.findViewById(R.id.login_email);
        passwordInput = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);
        progressBar = view.findViewById(R.id.progress_bar);

        nstuApi = NstuClient.getClient();

        loginButton.setOnClickListener(v -> attemptLogin());

        return view;
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        Log.d(TAG, "=== ПОПЫТКА ВХОДА ===");
        Log.d(TAG, "Email: " + email);

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);


        nstuApi.loginUser(email).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);

                Log.d(TAG, "HTTP код: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Найдено пользователей: " + response.body().size());

                    if (!response.body().isEmpty()) {
                        User user = response.body().get(0);
                        Log.d(TAG, "Найден: " + user.getEmail());
                        Log.d(TAG, "Пароль в БД: " + user.getPassword());
                        Log.d(TAG, "Введен пароль: " + password);

                        if (user.getPassword().equals(password)) {
                            Log.d(TAG, "ПАРОЛЬ ВЕРНЫЙ!");

                            Toast.makeText(getContext(), "Добро пожаловать, " + user.getFirst_name(), Toast.LENGTH_LONG).show();

                            getActivity().getSharedPreferences("app_prefs", getContext().MODE_PRIVATE)
                                    .edit()
                                    .putInt("user_id", user.getId())
                                    .putString("user_email", user.getEmail())
                                    .putString("user_first_name", user.getFirst_name())
                                    .putString("user_last_name", user.getLast_name())
                                    .putString("user_country", user.getCountry())
                                    .putString("user_type", user.getUser_type())
                                    .putString("user_university", user.getUniversity())
                                    .putBoolean("is_logged_in", true)
                                    .apply();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Log.e(TAG, "ПАРОЛЬ НЕВЕРНЫЙ!");
                            Toast.makeText(getContext(), "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Пользователь не найден");
                        Toast.makeText(getContext(), "Пользователь с таким email не найден", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Ошибка ответа: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Ошибка: " + response.errorBody().string());
                        } catch (Exception e) {}
                    }
                    Toast.makeText(getContext(), "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e(TAG, "Сетевая ошибка: " + t.getMessage());
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}