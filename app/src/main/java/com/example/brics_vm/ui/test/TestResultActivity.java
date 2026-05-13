package com.example.brics_vm.ui.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.brics_vm.R;
import com.example.brics_vm.MainActivity;

public class TestResultActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "score";
    public static final String EXTRA_TOTAL = "total";
    public static final String EXTRA_PERCENTAGE = "percentage";
    public static final String EXTRA_PASSED = "passed";
    public static final String EXTRA_COURSE_ID = "course_id";

    private ImageView resultIcon;
    private TextView resultTitle;
    private TextView scoreText;
    private TextView detailsText;
    private TextView passedText;
    private Button backButton;

    private int score;
    private int total;
    private int percentage;
    private boolean passed;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        score = getIntent().getIntExtra(EXTRA_SCORE, 0);
        total = getIntent().getIntExtra(EXTRA_TOTAL, 0);
        percentage = getIntent().getIntExtra(EXTRA_PERCENTAGE, 0);
        passed = getIntent().getBooleanExtra(EXTRA_PASSED, false);
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);

        initViews();
        setupToolbar();
        displayResult();
    }

    private void initViews() {
        resultIcon = findViewById(R.id.result_icon);
        resultTitle = findViewById(R.id.result_title);
        scoreText = findViewById(R.id.score_text);
        detailsText = findViewById(R.id.details_text);
        passedText = findViewById(R.id.passed_text);
        backButton = findViewById(R.id.back_button);

        // ВАРИАНТ 3: Переход на главный экран (Marketplace)
        backButton.setOnClickListener(v -> {
            // Переход в MainActivity на вкладку маркетплейса
            Intent intent = new Intent(TestResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("navigate_to_marketplace", true);
            startActivity(intent);
            finish();
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Результат теста");
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void displayResult() {
        scoreText.setText(percentage + "%");
        detailsText.setText("Правильных ответов: " + score + "/" + total);

        if (passed) {
            resultIcon.setImageResource(R.drawable.ic_success);
            resultTitle.setText("Тест сдан!");
            passedText.setText("✅ ПОЗДРАВЛЯЕМ!");
            passedText.setTextColor(getColor(android.R.color.holo_green_dark));
            passedText.setText("Курс завершён! Оценка: " + percentage + " баллов");
            backButton.setText("🏠 НА ГЛАВНУЮ");
        } else {
            resultIcon.setImageResource(R.drawable.ic_fail);
            resultTitle.setText("");
            passedText.setText("❌ К сожалению, вы не набрали проходной балл (50%)");
            passedText.setTextColor(getColor(android.R.color.holo_red_dark));
            backButton.setText("🏠 НА ГЛАВНУЮ");
        }
    }
}