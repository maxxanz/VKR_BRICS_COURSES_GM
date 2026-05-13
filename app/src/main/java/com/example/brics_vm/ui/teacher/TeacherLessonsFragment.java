package com.example.brics_vm.ui.teacher;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.adapters.LessonsAdapter;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.Lesson;
import com.example.brics_vm.models.TestResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherLessonsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LessonsAdapter adapter;
    private Button addLessonButton;
    private Button addTestButton;

    private NstuApi nstuApi;
    private int courseId;
    private List<Lesson> lessons = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_lessons, container, false);

        if (getArguments() != null) {
            courseId = getArguments().getInt("course_id");
        }

        initViews(view);
        loadLessons();

        addLessonButton.setOnClickListener(v -> showAddLessonDialog());
        addTestButton.setOnClickListener(v -> createTestForCourse());

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.lessons_recycler);
        addLessonButton = view.findViewById(R.id.add_lesson_button);
        addTestButton = view.findViewById(R.id.add_test_button);

        adapter = new LessonsAdapter(getContext(), courseId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadLessons() {
        nstuApi = NstuClient.getClient();
        nstuApi.getCourseLessons(courseId).enqueue(new Callback<List<Lesson>>() {
            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lessons = response.body();
                    adapter.setLessons(lessons);
                }
            }
            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка загрузки: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddLessonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_lesson, null);

        EditText titleInput = dialogView.findViewById(R.id.lesson_title_input);
        EditText contentInput = dialogView.findViewById(R.id.lesson_content_input);
        EditText videoUrlInput = dialogView.findViewById(R.id.lesson_video_url_input);
        EditText durationInput = dialogView.findViewById(R.id.lesson_duration_input);

        builder.setTitle("Добавить урок")
                .setView(dialogView)
                .setPositiveButton("СОХРАНИТЬ", (dialog, which) -> {
                    String title = titleInput.getText().toString().trim();
                    String content = contentInput.getText().toString().trim();
                    String videoUrl = videoUrlInput.getText().toString().trim();
                    int duration;
                    try {
                        duration = Integer.parseInt(durationInput.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Введите корректную длительность", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (title.isEmpty()) {
                        Toast.makeText(getContext(), "Введите название урока", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    createLesson(title, content, videoUrl, duration);
                })
                .setNegativeButton("ОТМЕНА", null)
                .show();
    }

    private void createLesson(String title, String content, String videoUrl, int duration) {
        Lesson newLesson = new Lesson();
        newLesson.setCourseId(courseId);
        newLesson.setTitle(title);
        newLesson.setTextContent(content);
        newLesson.setVideoUrl(videoUrl);
        newLesson.setDuration(duration);
        newLesson.setOrder(lessons.size() + 1);

        nstuApi = NstuClient.getClient();
        nstuApi.createLesson(newLesson).enqueue(new Callback<Lesson>() {
            @Override
            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Урок добавлен!", Toast.LENGTH_SHORT).show();
                    loadLessons(); // Обновляем список
                } else {
                    Toast.makeText(getContext(), "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Lesson> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createTestForCourse() {
        // Показываем уведомление о начале создания
        Toast.makeText(getContext(), "Создание теста...", Toast.LENGTH_SHORT).show();

        nstuApi.createCourseTest(courseId).enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int testId = response.body().getId();
                    if (testId > 0) {
                        Toast.makeText(getContext(), "Тест создан! ID: " + testId, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getContext(), AddQuestionActivity.class);
                        intent.putExtra("test_id", testId);
                        intent.putExtra("course_id", courseId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Ошибка: ID теста не получен", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Ошибка: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TestResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}