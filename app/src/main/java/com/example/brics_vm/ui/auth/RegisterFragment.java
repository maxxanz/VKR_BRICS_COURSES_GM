package com.example.brics_vm.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.brics_vm.AuthActivity;
import com.example.brics_vm.R;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private static final String TAG = "REGISTER_DEBUG";

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText countryInput;
    private EditText universityInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Spinner userTypeSpinner;
    private Button registerButton;
    private ProgressBar progressBar;
    private NstuApi nstuApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        firstNameInput = view.findViewById(R.id.register_first_name);
        lastNameInput = view.findViewById(R.id.register_last_name);
        emailInput = view.findViewById(R.id.register_email);
        countryInput = view.findViewById(R.id.register_country);
        universityInput = view.findViewById(R.id.register_university);
        passwordInput = view.findViewById(R.id.register_password);
        confirmPasswordInput = view.findViewById(R.id.register_confirm_password);
        userTypeSpinner = view.findViewById(R.id.register_user_type);
        registerButton = view.findViewById(R.id.register_button);
        progressBar = view.findViewById(R.id.progress_bar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        nstuApi = NstuClient.getClient();

        registerButton.setOnClickListener(v -> attemptRegistration());

        return view;
    }
    // Добавьте метод проверки email
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void attemptRegistration() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String country = countryInput.getText().toString().trim();
        String university = universityInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String userType = userTypeSpinner.getSelectedItem().toString().toLowerCase();

        Log.d(TAG, "=== РЕГИСТРАЦИЯ ===");
        Log.d(TAG, "Email: " + email);

        // Валидация полей
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                country.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // ПРОВЕРКА ФОРМАТА EMAIL
        if (!isValidEmail(email)) {
            Toast.makeText(getContext(), "Введите корректный email адрес", Toast.LENGTH_LONG).show();
            emailInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Пароль должен быть минимум 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);

        // ========== ПРОВЕРКА EMAIL ==========
        nstuApi.checkEmailExists(email).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d(TAG, "HTTP код проверки email: " + response.code());

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Email уже существует - показываем ошибку
                    progressBar.setVisibility(View.GONE);
                    registerButton.setEnabled(true);
                    Toast.makeText(getContext(), "Пользователь с таким email уже существует", Toast.LENGTH_LONG).show();
                } else {
                    // Email свободен - регистрируем
                    User newUser = new User(email, password, firstName, lastName,
                            country, userType, university);

                    Log.d(TAG, "Регистрация пользователя: " + email);

                    nstuApi.registerUser(newUser).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            progressBar.setVisibility(View.GONE);
                            registerButton.setEnabled(true);

                            Log.d(TAG, "HTTP код регистрации: " + response.code());

                            if (response.isSuccessful() || response.code() == 201) {
                                Toast.makeText(getContext(), "Регистрация успешна! Теперь войдите", Toast.LENGTH_LONG).show();

                                if (getActivity() instanceof AuthActivity) {
                                    ((AuthActivity) getActivity()).switchToLoginTab();
                                }
                            } else {
                                String errorMsg = "Ошибка регистрации: " + response.code();
                                try {
                                    if (response.errorBody() != null) {
                                        errorMsg = response.errorBody().string();
                                        Log.e(TAG, "Ошибка: " + errorMsg);
                                    }
                                } catch (Exception e) {}
                                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            registerButton.setEnabled(true);

                            Log.e(TAG, "Ошибка сети регистрации: " + t.getMessage());

                            if (t.getMessage() != null && t.getMessage().contains("End of input")) {
                                Toast.makeText(getContext(), "Регистрация успешна! Теперь войдите", Toast.LENGTH_LONG).show();

                                if (getActivity() instanceof AuthActivity) {
                                    ((AuthActivity) getActivity()).switchToLoginTab();
                                }
                            } else {
                                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                registerButton.setEnabled(true);
                Log.e(TAG, "Ошибка сети проверки email: " + t.getMessage());
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }}
