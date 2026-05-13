package com.example.brics_vm.ui.courses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.adapters.CoursesProgressAdapter;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.UserCourse;
import com.example.brics_vm.ui.coursedetail.CourseDetailActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoursesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private CoursesProgressAdapter adapter;
    private NstuApi nstuApi;
    private int currentUserId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_courses, container, false);

        recyclerView = root.findViewById(R.id.courses_recycler);
        progressBar = root.findViewById(R.id.courses_progress);
        emptyText = root.findViewById(R.id.empty_text);

        // Получаем ID текущего пользователя
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", getContext().MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        if (currentUserId == -1) {
            emptyText.setText("Ошибка: пользователь не найден");
            emptyText.setVisibility(View.VISIBLE);
            return root;
        }

        adapter = new CoursesProgressAdapter();

        // Обработка клика на курс
        adapter.setOnCourseClickListener(course -> {
            Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
            intent.putExtra(CourseDetailActivity.EXTRA_COURSE, course);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        nstuApi = NstuClient.getClient();
        loadUserCourses();

        return root;
    }

    private void loadUserCourses() {
        progressBar.setVisibility(View.VISIBLE);

        nstuApi.getUserCoursesWithDetails(currentUserId).enqueue(new Callback<List<UserCourse>>() {
            @Override
            public void onResponse(Call<List<UserCourse>> call, Response<List<UserCourse>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<UserCourse> courses = response.body();
                    if (courses.isEmpty()) {
                        emptyText.setVisibility(View.VISIBLE);
                        emptyText.setText("У вас пока нет курсов.\nНачните обучение с каталога!");
                    } else {
                        emptyText.setVisibility(View.GONE);
                        adapter.setCourses(courses);
                    }
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText("Ошибка загрузки: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<UserCourse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText("Ошибка сети: " + t.getMessage());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        loadUserCourses(); // перезагружаем при возврате
    }
}