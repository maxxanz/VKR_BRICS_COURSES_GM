package com.example.brics_vm.ui.teacher;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.brics_vm.R;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.Course;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCourseFragment extends Fragment {

    private EditText titleInput;
    private EditText subjectInput;
    private EditText descriptionInput;
    private EditText courseNumberInput;
    private EditText durationInput;
    private EditText imageUrlInput;
    private Button createButton;
    private ProgressBar progressBar;

    private NstuApi nstuApi;
    private int userId;
    private int createdCourseId = -1;

    private static final int PICK_FILE_REQUEST = 1001;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_course, container, false);

        userId = getActivity().getSharedPreferences("app_prefs", getContext().MODE_PRIVATE)
                .getInt("user_id", -1);

        initViews(view);

        createButton.setOnClickListener(v -> createCourse());

        return view;
    }

    private void initViews(View view) {
        titleInput = view.findViewById(R.id.course_title_input);
        subjectInput = view.findViewById(R.id.course_subject_input);
        descriptionInput = view.findViewById(R.id.course_description_input);
        courseNumberInput = view.findViewById(R.id.course_number_input);
        durationInput = view.findViewById(R.id.course_duration_input);
        imageUrlInput = view.findViewById(R.id.course_image_url_input);
        createButton = view.findViewById(R.id.create_course_button);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void createCourse() {
        String title = titleInput.getText().toString().trim();
        String subject = subjectInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String imageUrl = imageUrlInput.getText().toString().trim();

        int courseNumber;
        int duration;

        try {
            courseNumber = Integer.parseInt(courseNumberInput.getText().toString().trim());
            duration = Integer.parseInt(durationInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Введите корректные числа", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Введите название курса", Toast.LENGTH_SHORT).show();
            return;
        }

        if (subject.isEmpty()) {
            Toast.makeText(getContext(), "Введите предмет курса", Toast.LENGTH_SHORT).show();
            return;
        }

        Course newCourse = new Course();
        newCourse.setTitle(title);
        newCourse.setSubject(subject);
        newCourse.setDescription(description);
        newCourse.setImageUrl(imageUrl);
        newCourse.setCourseNumber(courseNumber);
        newCourse.setDuration(duration);
        newCourse.setCreatorId(userId);

        createButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        nstuApi = NstuClient.getClient();
        nstuApi.createCourse(newCourse).enqueue(new Callback<Course>() {
            @Override
            public void onResponse(Call<Course> call, Response<Course> response) {
                createButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    createdCourseId = response.body().getId();
                    Toast.makeText(getContext(), "Курс создан!", Toast.LENGTH_SHORT).show();

                    // Показываем диалог выбора: импорт syllabus или ручное добавление
                    showChoiceDialog();
                } else {
                    String error = "Ошибка: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            error += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Course> call, Throwable t) {
                createButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showChoiceDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("📚 Что дальше?")
                .setMessage("Курс создан. Как вы хотите его наполнить?")
                .setPositiveButton("📄 Загрузить файл", (dialog, which) -> {
                    pickFileForImport();
                })
                .setNegativeButton("✏️ Добавить вручную", (dialog, which) -> {
                    goToLessonsEdit();
                })
                .setNeutralButton("❌ Позже", null)
                .show();
    }

    private void goToLessonsEdit() {
        Bundle bundle = new Bundle();
        bundle.putInt("course_id", createdCourseId);
        Navigation.findNavController(requireView()).navigate(R.id.action_createCourse_to_teacherLessons, bundle);
    }

    private void pickFileForImport() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Выберите DOCX файл syllabus"), PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null && createdCourseId != -1) {
                importSyllabusFile(fileUri);
            } else if (createdCourseId == -1) {
                Toast.makeText(getContext(), "Ошибка: ID курса не найден", Toast.LENGTH_SHORT).show();
                goToLessonsEdit();
            }
        }
    }

    private void importSyllabusFile(Uri fileUri) {
        progressBar.setVisibility(View.VISIBLE);
        createButton.setEnabled(false);

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = getBytes(inputStream);

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
                    fileBytes
            );
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "syllabus.docx", requestFile);

            nstuApi = NstuClient.getClient();
            nstuApi.importSyllabus(createdCourseId, body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    progressBar.setVisibility(View.GONE);
                    createButton.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> result = response.body();
                        int lessonsCount = 0;
                        if (result.get("lessons_count") instanceof Double) {
                            lessonsCount = ((Double) result.get("lessons_count")).intValue();
                        } else if (result.get("lessons_count") instanceof Integer) {
                            lessonsCount = (Integer) result.get("lessons_count");
                        }

                        new AlertDialog.Builder(getContext())
                                .setTitle("✅ Импорт выполнен!")
                                .setMessage("Теперь вы можете отредактировать каждый урок (добавить текст и видео).")
                                .setPositiveButton("ПЕРЕЙТИ К УРОКАМ", (dialog, which) -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("course_id", createdCourseId);
                                    Navigation.findNavController(requireView()).navigate(R.id.action_createCourse_to_teacherLessons, bundle);
                                })
                                .show();
                    } else {
                        String error = "Ошибка импорта";
                        try {
                            if (response.errorBody() != null) {
                                error += ": " + response.errorBody().string();
                            }
                        } catch (Exception e) {}
                        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                        goToLessonsEdit();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    createButton.setEnabled(true);
                    Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    goToLessonsEdit();
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            createButton.setEnabled(true);
            Toast.makeText(getContext(), "Ошибка чтения файла: " + e.getMessage(), Toast.LENGTH_LONG).show();
            goToLessonsEdit();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}