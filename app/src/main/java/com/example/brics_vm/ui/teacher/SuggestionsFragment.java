package com.example.brics_vm.ui.teacher;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.adapters.SuggestionsAdapter;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.Suggestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private SuggestionsAdapter adapter;
    private NstuApi nstuApi;
    private int teacherId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggestions, container, false);

        // ========== ИНИЦИАЛИЗИРУЕМ teacherId ==========
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", requireContext().MODE_PRIVATE);
        teacherId = prefs.getInt("user_id", -1);
        // =============================================

        recyclerView = view.findViewById(R.id.suggestions_recycler);
        emptyText = view.findViewById(R.id.empty_text);

        adapter = new SuggestionsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnSuggestionActionListener(new SuggestionsAdapter.OnSuggestionActionListener() {
            @Override
            public void onApprove(Suggestion suggestion) {
                approveSuggestion(suggestion);
            }

            @Override
            public void onReject(Suggestion suggestion) {
                showRejectDialog(suggestion);
            }
        });

        loadSuggestions();

        return view;
    }

    private void loadSuggestions() {
        // Добавим проверку на валидный teacherId
        if (teacherId == -1) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("Ошибка: пользователь не авторизован");
            return;
        }

        nstuApi = NstuClient.getClient();
        nstuApi.getMyCourseSuggestions(teacherId).enqueue(new Callback<List<Suggestion>>() {
            @Override
            public void onResponse(Call<List<Suggestion>> call, Response<List<Suggestion>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    adapter.setSuggestions(response.body());
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText("Нет входящих предложений");
                }
            }

            @Override
            public void onFailure(Call<List<Suggestion>> call, Throwable t) {
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText("Ошибка: " + t.getMessage());
            }
        });
    }

    private void approveSuggestion(Suggestion suggestion) {
        if (teacherId == -1) return;

        nstuApi.approveSuggestion(suggestion.getId(), teacherId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "✅ Урок принят и добавлен в курс!", Toast.LENGTH_LONG).show();
                    loadSuggestions();
                } else {
                    Toast.makeText(getContext(), "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRejectDialog(Suggestion suggestion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Отклонить предложение");

        final EditText input = new EditText(getContext());
        input.setHint("Причина отклонения (необязательно)");
        builder.setView(input);

        builder.setPositiveButton("Отклонить", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            rejectSuggestion(suggestion, reason);
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void rejectSuggestion(Suggestion suggestion, String reason) {
        if (teacherId == -1) return;

        Map<String, String> body = new HashMap<>();
        body.put("reason", reason);

        nstuApi.rejectSuggestion(suggestion.getId(), teacherId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "❌ Предложение отклонено", Toast.LENGTH_SHORT).show();
                    loadSuggestions();
                } else {
                    Toast.makeText(getContext(), "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}