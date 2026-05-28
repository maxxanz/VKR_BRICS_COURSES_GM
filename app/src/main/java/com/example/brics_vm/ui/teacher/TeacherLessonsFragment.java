package com.example.brics_vm.ui.teacher;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.adapters.LessonsAdapter;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.Lesson;
import com.example.brics_vm.models.TestResponse;
import com.example.brics_vm.ui.lessons.LessonDetailActivity;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherLessonsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LessonsAdapter adapter;
    private Button addLessonButton;
    private Button addBricsLessonButton;
    private Button addTestButton;

    private NstuApi nstuApi;
    private int courseId;
    private List<Lesson> lessons = new ArrayList<>();
    private String teacherCountry;
    private boolean isBricsLessonAdded = false;

    // Роль пользователя
    private String userRole; // "teacher" или "student"

    @Override
    public void onResume() {
        super.onResume();
        loadLessons(); // перезагружаем уроки
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_lessons, container, false);

        if (getArguments() != null) {
            courseId = getArguments().getInt("course_id");
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", requireContext().MODE_PRIVATE);
        teacherCountry = prefs.getString("user_country", "стране");
        userRole = prefs.getString("user_type", "student"); // "teacher" или "student"

        initViews(view);
        loadLessons();

        // Показываем кнопки добавления только для преподавателя
        if (!userRole.equals("teacher")) {
            addLessonButton.setVisibility(View.GONE);
            addBricsLessonButton.setVisibility(View.GONE);
            addTestButton.setVisibility(View.GONE);
        } else {
            addLessonButton.setOnClickListener(v -> showAddLessonDialog(false));
            addBricsLessonButton.setOnClickListener(v -> checkBeforeAddBricsLesson());
            addTestButton.setOnClickListener(v -> createTestForCourse());
        }

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.lessons_recycler);
        addLessonButton = view.findViewById(R.id.add_lesson_button);
        addBricsLessonButton = view.findViewById(R.id.add_brics_lesson_button);
        addTestButton = view.findViewById(R.id.add_test_button);

        adapter = new LessonsAdapter(getContext(), courseId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Настройка клика по уроку в зависимости от роли
        adapter.setOnItemClickListener(lesson -> {
            if (userRole.equals("teacher")) {
                // Преподаватель → редактирование
                Intent intent = new Intent(getContext(), EditLessonActivity.class);
                intent.putExtra("lesson_id", lesson.getId());
                intent.putExtra("course_id", courseId);
                intent.putExtra("lesson_title", lesson.getTitle());
                intent.putExtra("lesson_content", lesson.getTextContent());
                intent.putExtra("lesson_video_url", lesson.getVideoUrl());
                intent.putExtra("lesson_duration", lesson.getDuration());
                startActivity(intent);
            } else {
                // Студент → просмотр
                Intent intent = new Intent(getContext(), LessonDetailActivity.class);
                intent.putExtra("lesson_id", lesson.getId());
                intent.putExtra("course_id", courseId);
                startActivity(intent);
            }
        });
    }

    private void loadLessons() {
        nstuApi = NstuClient.getClient();
        nstuApi.getCourseLessons(courseId).enqueue(new Callback<List<Lesson>>() {
            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lessons = response.body();
                    adapter.setLessons(lessons);

                    // Проверяем, есть ли уже BRICS урок (только для преподавателя)
                    if (userRole.equals("teacher")) {
                        for (Lesson lesson : lessons) {
                            if (lesson.getTitle() != null &&
                                    (lesson.getTitle().startsWith("🌍") ||
                                            lesson.getTitle().contains("BRICS"))) {
                                isBricsLessonAdded = true;
                                addLessonButton.setEnabled(false);
                                addLessonButton.setText("✅ УРОКИ ДОБАВЛЕНЫ");
                                addLessonButton.setAlpha(0.6f);
                                break;
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка загрузки: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Остальные методы (checkBeforeAddBricsLesson, showAddLessonDialog, createLesson, createTestForCourse)
    // остаются без изменений, так как они вызываются только для преподавателя
    // ... (копируйте их из вашего существующего файла)

    private void checkBeforeAddBricsLesson() {
        if (isBricsLessonAdded) {
            new AlertDialog.Builder(getContext())
                    .setTitle("⚠️ Внимание")
                    .setMessage("BRICS урок уже добавлен в этот курс.\n\n" +
                            "Вы можете добавить только один BRICS урок в курс, " +
                            "и он должен быть последним.")
                    .setPositiveButton("Понятно", null)
                    .show();
            return;
        }

        if (lessons.isEmpty()) {
            new AlertDialog.Builder(getContext())
                    .setTitle("❌ Нельзя создать BRICS урок")
                    .setMessage("Сначала добавьте хотя бы один обычный урок.\n\n" +
                            "BRICS урок должен быть заключительным и основываться на материале курса.")
                    .setPositiveButton("Добавить урок", (dialog, which) -> showAddLessonDialog(false))
                    .setNegativeButton("Отмена", null)
                    .show();
            return;
        }

        boolean hasEmptyLesson = false;
        for (Lesson lesson : lessons) {
            if (lesson.getTitle() == null || lesson.getTitle().isEmpty() ||
                    lesson.getTextContent() == null || lesson.getTextContent().isEmpty()) {
                hasEmptyLesson = true;
                break;
            }
        }

        if (hasEmptyLesson) {
            new AlertDialog.Builder(getContext())
                    .setTitle("⚠️ Заполните все уроки")
                    .setMessage("Пожалуйста, заполните название и содержание всех добавленных уроков перед созданием BRICS урока.\n\n" +
                            "BRICS урок - это итоговый урок, который обобщает материал.")
                    .setPositiveButton("Понятно", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("🌍 Создание BRICS урока")
                .setMessage("Вы создаёте заключительный BRICS урок:\n\n" +
                        "• Он будет посвящён применению курса в " + teacherCountry + "\n" +
                        "• ПОСЛЕ СОЗДАНИЯ этого урока вы НЕ СМОЖЕТЕ добавить обычные уроки\n" +
                        "• BRICS урок должен быть последним в курсе\n\n" +
                        "Вы уверены, что все обычные уроки добавлены и заполнены?")
                .setPositiveButton("✅ Да, создать BRICS урок", (dialog, which) -> showAddLessonDialog(true))
                .setNegativeButton("❌ Отмена, добавить ещё уроки", null)
                .show();
    }

    private void showAddLessonDialog(boolean isBrics) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_lesson, null);

        EditText titleInput = dialogView.findViewById(R.id.lesson_title_input);
        EditText contentInput = dialogView.findViewById(R.id.lesson_content_input);
        EditText videoUrlInput = dialogView.findViewById(R.id.lesson_video_url_input);
        EditText durationInput = dialogView.findViewById(R.id.lesson_duration_input);

        if (isBrics) {
            String defaultTitle = "🌍 " + teacherCountry + ". Применение курса.";
            titleInput.setText(defaultTitle);
            titleInput.setHint("Название BRICS урока");
            titleInput.setEnabled(false);
            contentInput.setHint("Расскажите, как тема курса применяется в " + teacherCountry +
                    "\n\nПриведите примеры из местной практики\n" +
                    "Предложите идеи для международного сотрудничества\n" +
                    "Обобщите ключевые выводы курса");
            builder.setTitle("🌍 Создание BRICS урока");
        } else {
            builder.setTitle("Добавление урока");
        }

        builder.setView(dialogView)
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

                    if (title.isEmpty() || content.isEmpty()) {
                        Toast.makeText(getContext(), "Заполните название и содержание урока", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isBrics && !title.startsWith("🌍")) {
                        title = "🌍 " + title;
                    }

                    createLesson(title, content, videoUrl, duration, isBrics);
                })
                .setNegativeButton("ОТМЕНА", null)
                .show();
    }

    private void createLesson(String title, String content, String videoUrl, int duration, boolean isBrics) {
        Lesson newLesson = new Lesson();
        newLesson.setCourseId(courseId);
        newLesson.setTitle(title);
        newLesson.setTextContent(content);
        newLesson.setVideoUrl(videoUrl);
        newLesson.setDuration(duration);
        newLesson.setOrder(lessons.size() + 1);

        nstuApi = NstuClient.getClient();

        newLesson.setBrics(isBrics);

        Toast.makeText(getContext(), "Создание урока...", Toast.LENGTH_SHORT).show();

        nstuApi.createLesson(newLesson).enqueue(new Callback<Lesson>() {
            @Override
            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = isBrics ? "🌍 BRICS урок создан! Теперь добавьте тест." : "Урок добавлен!";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    if (isBrics) {
                        isBricsLessonAdded = true;
                        addLessonButton.setEnabled(false);
                        addLessonButton.setText("✅ УРОКИ ДОБАВЛЕНЫ");
                        addLessonButton.setAlpha(0.6f);
                        addBricsLessonButton.setEnabled(false);
                        addBricsLessonButton.setText("🌍 BRICS УРОК СОЗДАН");
                        addBricsLessonButton.setAlpha(0.6f);
                    }

                    loadLessons();
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
            public void onFailure(Call<Lesson> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createTestForCourse() {
        Toast.makeText(getContext(), "Создание теста...", Toast.LENGTH_SHORT).show();

        nstuApi.createCourseTest(courseId).enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int testId = response.body().getId();
                    if (testId > 0) {
                        Toast.makeText(getContext(), "Тест создан! Теперь добавьте вопросы", Toast.LENGTH_LONG).show();
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