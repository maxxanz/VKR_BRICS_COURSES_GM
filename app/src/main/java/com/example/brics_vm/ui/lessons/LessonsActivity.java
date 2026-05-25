package com.example.brics_vm.ui.lessons;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.adapters.LessonsAdapter;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.Course;
import com.example.brics_vm.models.Lesson;
import com.example.brics_vm.ui.test.TestActivity;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Button;
import android.widget.Toast;

public class LessonsActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE = "course";

    private Toolbar toolbar;
    private TextView courseTitle;
    private ProgressBar progressBar;
    private TextView progressText;
    private RecyclerView recyclerView;
    private LessonsAdapter adapter;
    private NstuApi nstuApi;

    private LinearLayout testButtonContainer;
    private Button testButton;

    private Course course;
    private List<Lesson> lessons = new ArrayList<>();
    private List<Integer> completedLessonIds = new ArrayList<>();
    private int userId;

    private int testId = -1;
    private static final int REQUEST_LESSON_DETAIL = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        course = (Course) getIntent().getSerializableExtra(EXTRA_COURSE);
        if (course == null) {
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        initViews();
        setupToolbar();

        // Инициализация адаптера и установка слушателя кликов
        adapter = new LessonsAdapter(this, course.getId());
        adapter.setOnItemClickListener(lesson -> {
            Intent intent = new Intent(LessonsActivity.this, LessonDetailActivity.class);
            intent.putExtra(LessonDetailActivity.EXTRA_LESSON, lesson);
            intent.putExtra(LessonDetailActivity.EXTRA_COURSE_ID, course.getId());
            startActivityForResult(intent, REQUEST_LESSON_DETAIL);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadLessons();
        loadProgress();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        courseTitle = findViewById(R.id.course_title);
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);
        recyclerView = findViewById(R.id.lessons_recycler);
        testButtonContainer = findViewById(R.id.test_button_container);
        testButton = findViewById(R.id.test_button);

        courseTitle.setText(course.getTitle());

        adapter = new LessonsAdapter(this, course.getId());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        testButton.setOnClickListener(v -> {
            Intent intent = new Intent(LessonsActivity.this, TestActivity.class);
            intent.putExtra(TestActivity.EXTRA_COURSE_ID, course.getId());
            intent.putExtra(TestActivity.EXTRA_TEST_ID, testId);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void loadLessons() {
        nstuApi = NstuClient.getClient();


        nstuApi.getCourseLessons(course.getId()).enqueue(new Callback<List<Lesson>>() {
            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lessons = response.body();
                    adapter.setLessons(lessons);
                    updateProgressDisplay();
                }
            }

            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                Toast.makeText(LessonsActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProgress() {
        if (userId == -1) return;

        nstuApi.getUserLessonProgress(userId, course.getId()).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    completedLessonIds = response.body();
                    adapter.setCompletedLessons(completedLessonIds);
                    updateProgressDisplay();
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Toast.makeText(LessonsActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgressDisplay() {
        if (lessons.isEmpty()) return;

        int total = lessons.size();
        int completed = completedLessonIds.size();
        int percent = (int) ((double) completed / total * 100);

        progressBar.setProgress(percent);
        progressText.setText(percent + "% завершено (" + completed + "/" + total + " уроков)");

        if (completed == total && total > 0) {
            showTestButton();
        }
    }

    private void showTestButton() {
        LinearLayout testContainer = findViewById(R.id.test_button_container);
        Button testButton = findViewById(R.id.test_button);

        testContainer.setVisibility(View.VISIBLE);
        testButton.setOnClickListener(v -> {
            loadTestAndStart();
        });
    }

    private void loadTestAndStart() {
        nstuApi.getCourseTest(course.getId()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int testId = response.body();
                    Intent intent = new Intent(LessonsActivity.this, TestActivity.class);
                    intent.putExtra(TestActivity.EXTRA_COURSE_ID, course.getId());
                    intent.putExtra(TestActivity.EXTRA_TEST_ID, testId);
                    startActivity(intent);
                } else {
                    Toast.makeText(LessonsActivity.this, "Тест пока не создан", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(LessonsActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            int lessonId = data.getIntExtra("lesson_id", -1);
            boolean isCompleted = data.getBooleanExtra("is_completed", false);

            if (lessonId != -1) {
                if (isCompleted && !completedLessonIds.contains(lessonId)) {
                    completedLessonIds.add(lessonId);
                } else if (!isCompleted && completedLessonIds.contains(lessonId)) {
                    completedLessonIds.remove(Integer.valueOf(lessonId));
                }
                adapter.setCompletedLessons(completedLessonIds);
                updateProgressDisplay();
            }
        }
    }
}