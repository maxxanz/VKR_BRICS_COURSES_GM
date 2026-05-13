package com.example.brics_vm.ui.coursedetail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.brics_vm.R;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.Course;
import com.example.brics_vm.models.UserCourse;
import com.example.brics_vm.ui.lessons.LessonsActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE = "course";

    private ImageView courseImage;
    private TextView courseTitle;
    private TextView courseDescription;
    private TextView creatorName;
    private TextView creatorCountry;
    private TextView courseNumber;
    private TextView courseDuration;
    private TextView courseRating;
    private Button startButton;
    private Button continueButton;
    private Button completedButton;

    private Course course;
    private NstuApi nstuApi;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        course = (Course) getIntent().getSerializableExtra(EXTRA_COURSE);

        if (course == null) {
            Toast.makeText(this, "Ошибка: курс не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Получаем ID пользователя
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        initViews();
        setupToolbar();
        displayCourseInfo();
        checkUserCourseStatus();
    }

    private void initViews() {
        courseImage = findViewById(R.id.course_image);
        courseTitle = findViewById(R.id.course_title);
        courseDescription = findViewById(R.id.course_description);
        creatorName = findViewById(R.id.creator_name);
        creatorCountry = findViewById(R.id.creator_country);
        courseNumber = findViewById(R.id.course_number);
        courseDuration = findViewById(R.id.course_duration);
        courseRating = findViewById(R.id.course_rating);
        startButton = findViewById(R.id.start_button);
        continueButton = findViewById(R.id.continue_button);
        completedButton = findViewById(R.id.completed_button);

        startButton.setOnClickListener(v -> startCourse());
        continueButton.setOnClickListener(v -> goToLessons());
        completedButton.setOnClickListener(v -> goToLessons());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayCourseInfo() {
        android.util.Log.d("COURSE_DETAIL", "=== ДАННЫЕ КУРСА ===");
        android.util.Log.d("COURSE_DETAIL", "Title: " + course.getTitle());
        android.util.Log.d("COURSE_DETAIL", "CreatorFirstName: " + course.getCreatorFirstName());
        android.util.Log.d("COURSE_DETAIL", "CreatorFullName: " + course.getCreatorFullName());
        android.util.Log.d("COURSE_DETAIL", "CreatorCountry: " + course.getCreatorCountry());

        courseTitle.setText(course.getTitle());

        if (course.getDescription() != null && !course.getDescription().isEmpty()) {
            courseDescription.setText(course.getDescription());
        } else {
            courseDescription.setText("Описание отсутствует");
        }

        creatorName.setText(course.getCreatorFullName());
        creatorCountry.setText(course.getCreatorCountry());
        courseNumber.setText(course.getCourseNumberDisplay());
        courseDuration.setText(course.getDurationDisplay());

        if (course.getRating() > 0) {
            courseRating.setText("★ " + course.getRatingDisplay());
        } else {
            courseRating.setText("★ Нет оценок");
        }

        if (course.getImageUrl() != null && !course.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(course.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(courseImage);
        }
    }

    private void checkUserCourseStatus() {
        if (userId == -1) {
            // Не авторизован - показываем кнопку "Начать"
            showStartButton();
            return;
        }

        nstuApi = NstuClient.getClient();
        nstuApi.getUserCourseStatus(userId, course.getId()).enqueue(new Callback<List<UserCourse>>() {
            @Override
            public void onResponse(Call<List<UserCourse>> call, Response<List<UserCourse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserCourse uc = response.body().get(0);
                    String status = uc.getStatus();

                    if ("completed".equals(status)) {
                        showCompletedButton();
                    } else if ("in_progress".equals(status)) {
                        showContinueButton();
                    } else {
                        showStartButton();
                    }
                } else {
                    showStartButton();
                }
            }

            @Override
            public void onFailure(Call<List<UserCourse>> call, Throwable t) {
                showStartButton();
            }
        });
    }

    private void showStartButton() {
        startButton.setVisibility(android.view.View.VISIBLE);
        continueButton.setVisibility(android.view.View.GONE);
        completedButton.setVisibility(android.view.View.GONE);
    }

    private void showContinueButton() {
        startButton.setVisibility(android.view.View.GONE);
        continueButton.setVisibility(android.view.View.VISIBLE);
        completedButton.setVisibility(android.view.View.GONE);
    }

    private void showCompletedButton() {
        startButton.setVisibility(android.view.View.GONE);
        continueButton.setVisibility(android.view.View.GONE);
        completedButton.setVisibility(android.view.View.VISIBLE);
        completedButton.setEnabled(false);
        completedButton.setAlpha(0.7f);
    }

    private void startCourse() {
        if (userId == -1) {
            Toast.makeText(this, "Войдите в аккаунт, чтобы начать курс", Toast.LENGTH_SHORT).show();
            return;
        }

        UserCourse userCourse = new UserCourse();
        userCourse.setUserId(userId);
        userCourse.setCourseId(course.getId());
        userCourse.setStatus("in_progress");

        nstuApi.addToFavorites(userCourse).enqueue(new Callback<UserCourse>() {
            @Override
            public void onResponse(Call<UserCourse> call, Response<UserCourse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseDetailActivity.this, "Курс начат! Приятного обучения!", Toast.LENGTH_SHORT).show();
                    goToLessons();
                } else {
                    Toast.makeText(CourseDetailActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserCourse> call, Throwable t) {
                Toast.makeText(CourseDetailActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToLessons() {
        Intent intent = new Intent(CourseDetailActivity.this, LessonsActivity.class);
        intent.putExtra(LessonsActivity.EXTRA_COURSE, course);
        startActivity(intent);
    }
}