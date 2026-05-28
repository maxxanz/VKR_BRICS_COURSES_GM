package com.example.brics_vm.ui.lessons;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.brics_vm.R;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.Lesson;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonDetailActivity extends AppCompatActivity {

    public static final String EXTRA_LESSON = "lesson";
    public static final String EXTRA_COURSE_ID = "course_id";

    private Toolbar toolbar;
    private WebView videoWebView;
    private TextView textContent;
    private Button completeButton;
    private Button uncompleteButton;
    private Button playVideoButton; // Новая кнопка для внешнего плеера
    private NstuApi nstuApi;

    private Lesson lesson;
    private int courseId;
    private int userId;
    private boolean isCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        lesson = (Lesson) getIntent().getSerializableExtra(EXTRA_LESSON);
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        boolean isPreviewMode = getIntent().getBooleanExtra("is_preview_mode", false);

        if (lesson == null) {
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        initViews();
        setupToolbar();
        displayLesson();

        // Если режим предпросмотра — скрываем кнопки "Отметить пройденным"
        if (isPreviewMode) {
            completeButton.setVisibility(View.GONE);
            uncompleteButton.setVisibility(View.GONE);

            // Можно добавить индикатор, что это предпросмотр
            if (toolbar != null) {
                toolbar.setTitle("📋 Предпросмотр предложения");
            }
        } else {
            checkIfCompleted();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        videoWebView = findViewById(R.id.video_webview);
        textContent = findViewById(R.id.text_content);
        completeButton = findViewById(R.id.complete_button);
        uncompleteButton = findViewById(R.id.uncomplete_button);
        playVideoButton = findViewById(R.id.play_video_button); // Инициализация новой кнопки

        completeButton.setOnClickListener(v -> markComplete());
        uncompleteButton.setOnClickListener(v -> markUncomplete());

        // Обработчик для внешнего плеера
        if (playVideoButton != null) {
            playVideoButton.setOnClickListener(v -> openVideoExternally());
        }
    }

    /**
     * Открывает видео во внешнем плеере
     */
    private void openVideoExternally() {
        String videoUrl = lesson.getVideoUrl();
        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "Ссылка на видео отсутствует", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(videoUrl));
            startActivity(Intent.createChooser(intent, "Открыть видео через"));
        } catch (ActivityNotFoundException e) {
            // Если нет подходящего приложения, пробуем открыть в браузере
            openInBrowser(videoUrl);
        }
    }

    /**
     * Открывает видео в браузере (запасной вариант)
     */
    private void openInBrowser(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось открыть видео. Проверьте интернет-соединение.", Toast.LENGTH_LONG).show();
        }
    }

    private void setupToolbar() {
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                setSupportActionBar(toolbar);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("lesson_id", lesson.getId());
                resultIntent.putExtra("is_completed", isCompleted);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        }
    }

    private void displayLesson() {
        String videoUrl = lesson.getVideoUrl();

        if (videoUrl != null && !videoUrl.isEmpty()) {
            videoWebView.setVisibility(View.GONE);
            playVideoButton.setVisibility(View.VISIBLE);
        } else {
            videoWebView.setVisibility(View.GONE);
            if (playVideoButton != null) {
                playVideoButton.setVisibility(View.GONE);
            }
        }

        String content = lesson.getTextContent();
        if (content != null && !content.isEmpty()) {
            // ВАРИАНТ 1: Просто устанавливаем текст (сохраняет переносы строк)
            textContent.setText(content);
        }
    }

    private void checkIfCompleted() {
        if (userId == -1) return;

        nstuApi = NstuClient.getClient();

        nstuApi.getUserLessonProgress(userId, courseId).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isCompleted = response.body().contains(lesson.getId());
                    updateUIForState();
                } else {
                    isCompleted = false;
                    updateUIForState();
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                isCompleted = false;
                updateUIForState();
            }
        });
    }

    private void updateUIForState() {
        if (isCompleted) {
            completeButton.setVisibility(View.GONE);
            uncompleteButton.setVisibility(View.VISIBLE);
        } else {
            completeButton.setVisibility(View.VISIBLE);
            uncompleteButton.setVisibility(View.GONE);
        }
    }

    private void markComplete() {
        if (userId == -1 || isCompleted) return;

        Log.d("LESSON_DEBUG", "Marking lesson " + lesson.getId() + " as completed for user " + userId);

        showLoadingState(true);

        nstuApi.completeLesson(lesson.getId(), userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoadingState(false);
                Log.d("LESSON_DEBUG", "Response code: " + response.code());

                if (response.isSuccessful()) {
                    isCompleted = true;
                    updateUIForState();
                    Toast.makeText(LessonDetailActivity.this, "✅ Урок отмечен как пройденный!", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("lesson_id", lesson.getId());
                    resultIntent.putExtra("is_completed", true);
                    setResult(RESULT_OK, resultIntent);

                } else {
                    String errorMsg = "Ошибка: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                            Log.e("LESSON_DEBUG", "Error: " + errorMsg);
                        }
                    } catch (Exception e) {}
                    Toast.makeText(LessonDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoadingState(false);
                Log.e("LESSON_DEBUG", "Failure: " + t.getMessage());
                Toast.makeText(LessonDetailActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markUncomplete() {
        if (userId == -1 || !isCompleted) return;

        Log.d("LESSON_DEBUG", "Unmarking lesson " + lesson.getId() + " for user " + userId);

        showLoadingState(true);

        nstuApi.uncompleteLesson(lesson.getId(), userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoadingState(false);
                Log.d("LESSON_DEBUG", "Uncomplete response code: " + response.code());

                if (response.isSuccessful()) {
                    isCompleted = false;
                    updateUIForState();
                    Toast.makeText(LessonDetailActivity.this, "↩️ Отметка снята. Урок можно пройти заново.", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("lesson_id", lesson.getId());
                    resultIntent.putExtra("is_completed", false);
                    setResult(RESULT_OK, resultIntent);

                } else {
                    String errorMsg = "Ошибка: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                            Log.e("LESSON_DEBUG", "Error: " + errorMsg);
                        }
                    } catch (Exception e) {}
                    Toast.makeText(LessonDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoadingState(false);
                Log.e("LESSON_DEBUG", "Failure: " + t.getMessage());
                Toast.makeText(LessonDetailActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoadingState(boolean isLoading) {
        completeButton.setEnabled(!isLoading);
        uncompleteButton.setEnabled(!isLoading);
        if (isLoading) {
            completeButton.setText("⏳ Сохранение...");
            uncompleteButton.setText("⏳ Сохранение...");
        } else {
            completeButton.setText("✅ Отметить пройденным");
            uncompleteButton.setText("↩️ Снять отметку");
        }
    }
}