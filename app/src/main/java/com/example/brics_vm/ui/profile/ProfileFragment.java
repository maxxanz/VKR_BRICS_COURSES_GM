package com.example.brics_vm.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.brics_vm.R;

public class ProfileFragment extends Fragment {

    private TextView studentName;
    private TextView studentEmail;
    private TextView studentUserType;
    private TextView studentUniversity;
    private TextView studentCountryInfo;  // ← это поле для страны
    private ImageView studentAvatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализация - только существующие в XML элементы
        studentName = root.findViewById(R.id.student_name);
        studentEmail = root.findViewById(R.id.student_email);
        studentUserType = root.findViewById(R.id.student_user_type);
        studentUniversity = root.findViewById(R.id.student_university);
        studentCountryInfo = root.findViewById(R.id.student_country_info);  // ← страна здесь
        studentAvatar = root.findViewById(R.id.student_avatar);

        // Загружаем данные
        loadUserData();

        Button createCourseButton = root.findViewById(R.id.create_course_button);
        String userType = getActivity().getSharedPreferences("app_prefs", getContext().MODE_PRIVATE)
                .getString("user_type", "");

        if ("teacher".equals(userType)) {
            createCourseButton.setVisibility(View.VISIBLE);
            createCourseButton.setOnClickListener(v -> {
                Navigation.findNavController(getView()).navigate(R.id.action_profile_to_createCourse);
            });
        } else {
            createCourseButton.setVisibility(View.GONE);
        }

        return root;
    }

    private void loadUserData() {
        android.content.SharedPreferences prefs = getActivity()
                .getSharedPreferences("app_prefs", getContext().MODE_PRIVATE);

        String firstName = prefs.getString("user_first_name", "");
        String lastName = prefs.getString("user_last_name", "");
        String email = prefs.getString("user_email", "");
        String country = prefs.getString("user_country", "");
        String university = prefs.getString("user_university", "");
        String userType = prefs.getString("user_type", "");

        // Устанавливаем данные
        studentName.setText(firstName + " " + lastName);
        studentEmail.setText(email);
        studentUniversity.setText(university);

        // Страна - через правильный TextView
        if (studentCountryInfo != null) {
            studentCountryInfo.setText(country);
        }

        // Тип пользователя на русском
        switch (userType) {
            case "student":
                studentUserType.setText("Студент");
                break;
            case "teacher":
                studentUserType.setText("Преподаватель");
                break;
            case "admin":
                studentUserType.setText("Администратор");
                break;
            default:
                studentUserType.setText(userType);
        }
    }
}