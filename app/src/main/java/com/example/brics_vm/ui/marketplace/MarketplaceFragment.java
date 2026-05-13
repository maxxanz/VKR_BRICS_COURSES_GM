package com.example.brics_vm.ui.marketplace;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.adapters.MarketplaceAdapter;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.Course;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketplaceFragment extends Fragment {

    private static final String TAG = "MARKETPLACE";
    private RecyclerView recyclerView;
    private MarketplaceAdapter adapter;
    private ProgressBar progressBar;
    private NstuApi nstuApi;
    private List<Course> courseList = new ArrayList<>();

    private List<Course> allCourses = new ArrayList<>(); // ← добавьте это


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_marketplace, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        progressBar = root.findViewById(R.id.progressBar);

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Инициализация API
        nstuApi = NstuClient.getClient();

        // Загрузка курсов
        loadCoursesFromSupabase();

        // Показываем заглушку, пока грузится
        adapter = new MarketplaceAdapter(courseList);
        recyclerView.setAdapter(adapter);

        return root;
    }

    private void loadCoursesFromSupabase() {
        progressBar.setVisibility(View.VISIBLE);

        nstuApi.getAllCourses().enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Очищаем оба списка
                    courseList.clear();
                    allCourses.clear();

                    // Добавляем курсы в ОБА списка
                    courseList.addAll(response.body());
                    allCourses.addAll(response.body());  // ← ЭТО КЛЮЧЕВАЯ СТРОКА!

                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Загружено курсов: " + courseList.size());
                    Log.d(TAG, "Сохранено в allCourses: " + allCourses.size());
                } else {
                    Log.e(TAG, "Ошибка загрузки курсов: " + response.code());
                    Toast.makeText(getContext(), "Не удалось загрузить курсы", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Ошибка сети: " + t.getMessage());
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Добавьте этот метод в MarketplaceFragment
    public void filterCourses(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Показываем все курсы
            courseList.clear();
            courseList.addAll(allCourses);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Показаны все курсы: " + courseList.size());
        } else {
            List<Course> filtered = new ArrayList<>();
            String lowerQuery = query.toLowerCase().trim();

            // Разбиваем запрос на отдельные слова
            String[] words = lowerQuery.split("\\s+");

            for (Course course : allCourses) {
                String title = course.getTitle().toLowerCase();

                String[] titleWords = title.split("\\s+");

                boolean allWordsMatch = true;

                for (String word : words) {
                    boolean wordFound = false;

                    for (String titleWord : titleWords) {
                        if (titleWord.startsWith(word)) {
                            wordFound = true;
                            break;
                        }
                    }

                    if (!wordFound) {
                        allWordsMatch = false;
                        break;
                    }
                }

                if (allWordsMatch) {
                    filtered.add(course);
                }
            }
            courseList.clear();
            courseList.addAll(filtered);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Поиск '" + query + "' найден: " + filtered.size());
        }
    }


}