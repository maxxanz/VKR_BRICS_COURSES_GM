package com.example.brics_vm.ui.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.brics_vm.MainActivity;
import com.example.brics_vm.R;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.TestQuestion;

public class AddQuestionActivity extends AppCompatActivity {

    private EditText questionText;
    private EditText optionA, optionB, optionC, optionD;
    private RadioGroup correctGroup;
    private RadioButton correctA, correctB, correctC, correctD;
    private Button addBtn, finishBtn;

    private NstuApi nstuApi;
    private int testId;
    private int courseId;
    private int questionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        testId = getIntent().getIntExtra("test_id", -1);
        courseId = getIntent().getIntExtra("course_id", -1);

        if (testId == -1) {
            Toast.makeText(this, "Ошибка: test_id не передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        nstuApi = NstuClient.getClient();

        addBtn.setOnClickListener(v -> addQuestion());

        // ИСПРАВЛЕННАЯ КНОПКА ЗАВЕРШЕНИЯ
        finishBtn.setOnClickListener(v -> {
            // Показываем подтверждение
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Завершение")
                    .setMessage("Тест будет сохранён с текущими вопросами. Вы уверены?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        // Переход на главный экран с очисткой стека
                        Intent intent = new Intent(AddQuestionActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("navigate_to_marketplace", true);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });
    }

    private void initViews() {
        questionText = findViewById(R.id.question_text);
        optionA = findViewById(R.id.option_a);
        optionB = findViewById(R.id.option_b);
        optionC = findViewById(R.id.option_c);
        optionD = findViewById(R.id.option_d);
        correctGroup = findViewById(R.id.correct_group);
        correctA = findViewById(R.id.correct_a);
        correctB = findViewById(R.id.correct_b);
        correctC = findViewById(R.id.correct_c);
        correctD = findViewById(R.id.correct_d);
        addBtn = findViewById(R.id.add_btn);
        finishBtn = findViewById(R.id.finish_btn);
    }

    private void addQuestion() {
        String text = questionText.getText().toString().trim();
        String a = optionA.getText().toString().trim();
        String b = optionB.getText().toString().trim();
        String c = optionC.getText().toString().trim();
        String d = optionD.getText().toString().trim();

        String correct = "";
        int selected = correctGroup.getCheckedRadioButtonId();
        if (selected == correctA.getId()) correct = "A";
        else if (selected == correctB.getId()) correct = "B";
        else if (selected == correctC.getId()) correct = "C";
        else if (selected == correctD.getId()) correct = "D";

        if (text.isEmpty()) {
            Toast.makeText(this, "Введите текст вопроса", Toast.LENGTH_SHORT).show();
            return;
        }
        if (correct.isEmpty()) {
            Toast.makeText(this, "Выберите правильный ответ", Toast.LENGTH_SHORT).show();
            return;
        }

        TestQuestion q = new TestQuestion();
        q.setTestId(testId);
        q.setQuestionText(text);
        q.setOptionA(a);
        q.setOptionB(b);
        q.setOptionC(c);
        q.setOptionD(d);
        q.setCorrectAnswer(correct);
        q.setPoints(10);
        q.setOrder(questionCount + 1);

        addBtn.setEnabled(false);

        nstuApi.createQuestion(q).enqueue(new retrofit2.Callback<TestQuestion>() {
            @Override
            public void onResponse(retrofit2.Call<TestQuestion> call, retrofit2.Response<TestQuestion> response) {
                addBtn.setEnabled(true);
                if (response.isSuccessful()) {
                    questionCount++;
                    Toast.makeText(AddQuestionActivity.this, "Вопрос добавлен! (" + questionCount + ")", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(AddQuestionActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<TestQuestion> call, Throwable t) {
                addBtn.setEnabled(true);
                Toast.makeText(AddQuestionActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        questionText.setText("");
        optionA.setText("");
        optionB.setText("");
        optionC.setText("");
        optionD.setText("");
        correctGroup.clearCheck();
        questionText.requestFocus();
    }
}