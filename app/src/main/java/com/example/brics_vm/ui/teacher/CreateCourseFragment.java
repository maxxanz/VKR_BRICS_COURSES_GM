package com.example.brics_vm.ui.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.brics_vm.R;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.Course;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCourseFragment extends Fragment {

    private EditText titleInput;
    private EditText subjectInput;      // НОВОЕ
    private EditText descriptionInput;
    private EditText courseNumberInput;
    private EditText durationInput;
    private EditText imageUrlInput;
    private Button createButton;

    private NstuApi nstuApi;
    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_course, container, false);

        // Получаем ID текущего пользователя
        userId = getActivity().getSharedPreferences("app_prefs", getContext().MODE_PRIVATE)
                .getInt("user_id", -1);

        initViews(view);

        createButton.setOnClickListener(v -> createCourse());

        return view;
    }

    private void initViews(View view) {
        titleInput = view.findViewById(R.id.course_title_input);
        subjectInput = view.findViewById(R.id.course_subject_input);  // НОВОЕ
        descriptionInput = view.findViewById(R.id.course_description_input);
        courseNumberInput = view.findViewById(R.id.course_number_input);
        durationInput = view.findViewById(R.id.course_duration_input);
        imageUrlInput = view.findViewById(R.id.course_image_url_input);
        createButton = view.findViewById(R.id.create_course_button);
    }

    private void createCourse() {
        String title = titleInput.getText().toString().trim();
        String subject = subjectInput.getText().toString().trim();  // НОВОЕ
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
        newCourse.setSubject(subject);  // НОВОЕ - отправляем предмет на сервер
        newCourse.setDescription(description);
        newCourse.setImageUrl(imageUrl);
        newCourse.setCourseNumber(courseNumber);
        newCourse.setDuration(duration);
        newCourse.setCreatorId(userId);

        createButton.setEnabled(false);

        nstuApi = NstuClient.getClient();
        nstuApi.createCourse(newCourse).enqueue(new Callback<Course>() {
            @Override
            public void onResponse(Call<Course> call, Response<Course> response) {
                createButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Курс успешно создан!", Toast.LENGTH_SHORT).show();
                    // Переход к добавлению уроков
                    Bundle bundle = new Bundle();
                    bundle.putInt("course_id", response.body().getId());
                    Navigation.findNavController(getView()).navigate(R.id.action_createCourse_to_teacherLessons, bundle);
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
                Toast.makeText(getContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}