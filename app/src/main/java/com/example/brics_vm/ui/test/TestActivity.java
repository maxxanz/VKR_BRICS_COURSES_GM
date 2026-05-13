package com.example.brics_vm.ui.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.brics_vm.R;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.TestQuestion;
import com.example.brics_vm.models.TestResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "course_id";
    public static final String EXTRA_TEST_ID = "test_id";

    private Toolbar toolbar;
    private TextView questionCounter;
    private TextView timerText;
    private TextView questionText;
    private RadioGroup radioGroup;
    private RadioButton optionA, optionB, optionC, optionD;
    private Button prevButton, nextButton;

    private NstuApi nstuApi;
    private List<TestQuestion> questions = new ArrayList<>();
    private Map<Integer, String> userAnswers = new HashMap<>();
    private int currentIndex = 0;
    private int testId;
    private int courseId;
    private int userId;
    private CountDownTimer countDownTimer;
    private long timeLeft = 30 * 60 * 1000; // 30 минут в миллисекундах

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testId = getIntent().getIntExtra(EXTRA_TEST_ID, -1);
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);

        if (testId == -1) {
            Toast.makeText(this, "Ошибка: тест не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        initViews();
        setupToolbar();
        loadQuestions();
        startTimer();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        questionCounter = findViewById(R.id.question_counter);
        timerText = findViewById(R.id.timer);
        questionText = findViewById(R.id.question_text);
        radioGroup = findViewById(R.id.radio_group);
        optionA = findViewById(R.id.option_a);
        optionB = findViewById(R.id.option_b);
        optionC = findViewById(R.id.option_c);
        optionD = findViewById(R.id.option_d);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);

        prevButton.setOnClickListener(v -> previousQuestion());
        nextButton.setOnClickListener(v -> nextQuestion());
    }

    private void setupToolbar() {
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> showExitConfirmation());
        }
    }

    private void loadQuestions() {
        nstuApi = NstuClient.getClient();

        nstuApi.getTestQuestions(testId).enqueue(new Callback<List<TestQuestion>>() {
            @Override
            public void onResponse(Call<List<TestQuestion>> call, Response<List<TestQuestion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questions = response.body();
                    displayQuestion(0);
                    updateNavigationButtons();
                } else {
                    Toast.makeText(TestActivity.this, "Ошибка загрузки вопросов", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<TestQuestion>> call, Throwable t) {
                Toast.makeText(TestActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;

        TestQuestion q = questions.get(index);
        questionCounter.setText("Вопрос " + (index + 1) + " из " + questions.size());
        questionText.setText(q.getQuestionText());

        optionA.setText(q.getOptionA());
        optionB.setText(q.getOptionB());
        optionC.setText(q.getOptionC());
        optionD.setText(q.getOptionD());

        // Восстанавливаем выбранный ответ
        String savedAnswer = userAnswers.get(index);
        radioGroup.clearCheck();
        if (savedAnswer != null) {
            switch (savedAnswer) {
                case "A": optionA.setChecked(true); break;
                case "B": optionB.setChecked(true); break;
                case "C": optionC.setChecked(true); break;
                case "D": optionD.setChecked(true); break;
            }
        }
    }

    private void saveCurrentAnswer() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        String answer = null;
        if (selectedId == optionA.getId()) answer = "A";
        else if (selectedId == optionB.getId()) answer = "B";
        else if (selectedId == optionC.getId()) answer = "C";
        else if (selectedId == optionD.getId()) answer = "D";

        if (answer != null) {
            userAnswers.put(currentIndex, answer);
        }
    }

    private void nextQuestion() {
        saveCurrentAnswer();

        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            displayQuestion(currentIndex);
            updateNavigationButtons();
        } else {
            submitTest();
        }
    }

    private void previousQuestion() {
        saveCurrentAnswer();

        if (currentIndex > 0) {
            currentIndex--;
            displayQuestion(currentIndex);
            updateNavigationButtons();
        }
    }

    private void updateNavigationButtons() {
        if (currentIndex > 0) {
            prevButton.setVisibility(View.VISIBLE);
        } else {
            prevButton.setVisibility(View.GONE);
        }

        if (currentIndex == questions.size() - 1) {
            nextButton.setText("ЗАВЕРШИТЬ");
        } else {
            nextButton.setText("ДАЛЕЕ ▶");
        }
    }

    private void submitTest() {
        saveCurrentAnswer();

        // Проверяем, что все вопросы отвечены
        if (userAnswers.size() < questions.size()) {
            new AlertDialog.Builder(this)
                    .setTitle("Внимание!")
                    .setMessage("Вы ответили не на все вопросы. Вы уверены, что хотите завершить?")
                    .setPositiveButton("Да", (dialog, which) -> calculateResult())
                    .setNegativeButton("Нет", null)
                    .show();
        } else {
            calculateResult();
        }
    }

    private void calculateResult() {
        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            TestQuestion q = questions.get(i);
            String userAnswer = userAnswers.get(i);
            if (userAnswer != null && userAnswer.equals(q.getCorrectAnswer())) {
                correctCount++;
            }
        }

        int percentage = (int) ((double) correctCount / questions.size() * 100);
        boolean passed = percentage >= 50;

        // Сохраняем результат
        saveTestResult(correctCount, percentage, passed);
    }

    private void saveTestResult(int score, int percentage, boolean passed) {
        TestResult result = new TestResult();
        result.setUserId(userId);
        result.setTestId(testId);
        result.setScore(score);
        result.setTotalQuestions(questions.size());
        result.setPercentage(percentage);
        result.setPassed(passed);

        nstuApi.submitTestResult(result).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Обновляем user_courses
                    updateUserCourse(percentage);

                    Intent intent = new Intent(TestActivity.this, TestResultActivity.class);
                    intent.putExtra(TestResultActivity.EXTRA_SCORE, score);
                    intent.putExtra(TestResultActivity.EXTRA_TOTAL, questions.size());
                    intent.putExtra(TestResultActivity.EXTRA_PERCENTAGE, percentage);
                    intent.putExtra(TestResultActivity.EXTRA_PASSED, passed);
                    intent.putExtra(TestResultActivity.EXTRA_COURSE_ID, courseId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(TestActivity.this, "Ошибка сохранения результата", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TestActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserCourse(int percentage) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String completedAt = sdf.format(new Date());

        nstuApi.updateCourseResult(userId, courseId, percentage, "completed", completedAt)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("TEST", "Курс обновлён: completed");
                        // Курс теперь будет отображаться в "Пройденные"
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("TEST", "Ошибка: " + t.getMessage());
                    }
                });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                Toast.makeText(TestActivity.this, "Время вышло! Тест завершён.", Toast.LENGTH_LONG).show();
                submitTest();
            }
        }.start();
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Выход из теста")
                .setMessage("Ваш прогресс не будет сохранён. Вы уверены?")
                .setPositiveButton("Да", (dialog, which) -> finish())
                .setNegativeButton("Нет", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}