package com.example.brics_vm.ui.teacher;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.brics_vm.R;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.Lesson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditLessonActivity extends AppCompatActivity {

    private static final String TAG = "EDIT_LESSON";

    private EditText titleInput;
    private EditText contentInput;
    private EditText videoUrlInput;
    private EditText durationInput;
    private Button saveButton;

    private NstuApi nstuApi;
    private int lessonId;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate START");

        setContentView(R.layout.activity_edit_lesson);
        Log.d(TAG, "setContentView done");

        lessonId = getIntent().getIntExtra("lesson_id", -1);
        courseId = getIntent().getIntExtra("course_id", -1);
        String title = getIntent().getStringExtra("lesson_title");
        String content = getIntent().getStringExtra("lesson_content");
        String videoUrl = getIntent().getStringExtra("lesson_video_url");
        int duration = getIntent().getIntExtra("lesson_duration", 60);

        Log.d(TAG, "lessonId=" + lessonId + ", courseId=" + courseId);
        Log.d(TAG, "title=" + title);
        Log.d(TAG, "content=" + content);
        Log.d(TAG, "videoUrl=" + videoUrl);
        Log.d(TAG, "duration=" + duration);

        initViews();
        Log.d(TAG, "initViews done");

        titleInput.setText(title);
        contentInput.setText(content);
        videoUrlInput.setText(videoUrl);
        durationInput.setText(String.valueOf(duration));

        saveButton.setOnClickListener(v -> {
            Log.d(TAG, "saveButton CLICKED!");
            saveLesson();
        });

        Log.d(TAG, "onCreate END");
    }

    private void initViews() {
        titleInput = findViewById(R.id.edit_lesson_title);
        contentInput = findViewById(R.id.edit_lesson_content);
        videoUrlInput = findViewById(R.id.edit_lesson_video_url);
        durationInput = findViewById(R.id.edit_lesson_duration);
        saveButton = findViewById(R.id.save_lesson_button);
    }

    private void saveLesson() {
        Log.d(TAG, "saveLesson START");

        String newTitle = titleInput.getText().toString().trim();
        String newContent = contentInput.getText().toString().trim();
        String newVideoUrl = videoUrlInput.getText().toString().trim();
        int newDuration;

        Log.d(TAG, "newTitle=" + newTitle);
        Log.d(TAG, "newContent=" + newContent);
        Log.d(TAG, "newVideoUrl=" + newVideoUrl);

        try {
            newDuration = Integer.parseInt(durationInput.getText().toString().trim());
            Log.d(TAG, "newDuration=" + newDuration);
        } catch (NumberFormatException e) {
            Log.e(TAG, "NumberFormatException: " + e.getMessage());
            Toast.makeText(this, "Введите корректную длительность", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newTitle.isEmpty()) {
            Log.d(TAG, "Title is empty");
            Toast.makeText(this, "Введите название урока", Toast.LENGTH_SHORT).show();
            return;
        }

        Lesson updatedLesson = new Lesson();
        updatedLesson.setId(lessonId);
        updatedLesson.setCourseId(courseId);
        updatedLesson.setTitle(newTitle);
        updatedLesson.setTextContent(newContent);
        updatedLesson.setVideoUrl(newVideoUrl);
        updatedLesson.setDuration(newDuration);

        Log.d(TAG, "Sending updateLesson to server...");
        Log.d(TAG, "URL будет: http://217.71.129.139:4784/rest/v1/lessons/" + lessonId);

        nstuApi = NstuClient.getClient();
        Call<Lesson> call = nstuApi.updateLesson(lessonId, updatedLesson);

        call.enqueue(new Callback<Lesson>() {
            @Override
            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
                Log.d(TAG, "onResponse: code=" + response.code());
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ Lesson updated successfully!");
                    Toast.makeText(EditLessonActivity.this, "✅ Урок сохранён!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String error = "Ошибка: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            error += " - " + errorBody;
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(EditLessonActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Lesson> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(EditLessonActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}