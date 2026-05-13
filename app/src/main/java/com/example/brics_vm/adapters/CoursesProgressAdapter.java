package com.example.brics_vm.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.models.Course;
import com.example.brics_vm.models.UserCourse;
import com.example.brics_vm.ui.coursedetail.CourseDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class CoursesProgressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<UserCourse> inProgressCourses = new ArrayList<>();
    private List<UserCourse> completedCourses = new ArrayList<>();
    private List<UserCourse> failedCourses = new ArrayList<>();  // ← НОВЫЙ СПИСОК для несданных
    private List<Object> displayList = new ArrayList<>();

    private OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public void setOnCourseClickListener(OnCourseClickListener listener) {
        this.listener = listener;
    }

    public void setCourses(List<UserCourse> courses) {
        inProgressCourses.clear();
        completedCourses.clear();
        failedCourses.clear();

        for (UserCourse course : courses) {
            switch (course.getStatus()) {
                case "in_progress":
                    inProgressCourses.add(course);
                    break;
                case "completed":
                    // Проверяем результат: если >= 50% - пройден, иначе - не сдан
                    if (course.getResult() != null && course.getResult() >= 50) {
                        completedCourses.add(course);
                    } else {
                        failedCourses.add(course);
                    }
                    break;
            }
        }

        buildDisplayList();
        notifyDataSetChanged();
    }

    private void buildDisplayList() {
        displayList.clear();

        if (!inProgressCourses.isEmpty()) {
            displayList.add("🔄 В процессе (" + inProgressCourses.size() + ")");
            displayList.addAll(inProgressCourses);
        }

        if (!completedCourses.isEmpty()) {
            displayList.add("✅ Пройденные (" + completedCourses.size() + ")");
            displayList.addAll(completedCourses);
        }

        if (!failedCourses.isEmpty()) {
            displayList.add("❌ Не сданы (" + failedCourses.size() + ")");
            displayList.addAll(failedCourses);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return displayList.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_progress, parent, false);
            return new CourseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).title.setText((String) displayList.get(position));
        } else if (holder instanceof CourseViewHolder) {
            UserCourse userCourse = (UserCourse) displayList.get(position);
            ((CourseViewHolder) holder).bind(userCourse, listener);
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        HeaderViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.section_header);
        }
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, status, score;
        View cardView;

        CourseViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.course_title);
            status = itemView.findViewById(R.id.course_status);
            score = itemView.findViewById(R.id.course_score);
            cardView = itemView.findViewById(R.id.card_view);
        }

        void bind(UserCourse userCourse, OnCourseClickListener listener) {
            // Название курса
            String titleText = userCourse.getTitle();
            if (titleText != null && !titleText.isEmpty()) {
                title.setText(titleText);
            } else {
                title.setText("Курс " + userCourse.getCourseId());
            }

            // Определяем статус и цвет в зависимости от результата
            String statusText;
            int statusColor;

            if ("completed".equals(userCourse.getStatus())) {
                if (userCourse.getResult() != null && userCourse.getResult() >= 50) {
                    statusText = "✅ Пройден";
                    statusColor = android.R.color.holo_green_dark;
                } else {
                    statusText = "❌ Не сдан";
                    statusColor = android.R.color.holo_red_dark;
                }
            } else {
                statusText = userCourse.getStatusText();
                statusColor = userCourse.getStatusColor();
            }

            status.setText(statusText);
            try {
                status.setTextColor(itemView.getContext().getColor(statusColor));
            } catch (Exception e) {
                // fallback
            }

            // Показываем баллы если есть результат
            if (userCourse.getResult() != null) {
                score.setVisibility(View.VISIBLE);
                if (userCourse.getResult() >= 50) {
                    score.setText("⭐ Баллы: " + userCourse.getResult().intValue());
                    try {
                        score.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
                    } catch (Exception e) {}
                } else {
                    score.setText("📉 Баллы: " + userCourse.getResult().intValue());
                    try {
                        score.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                    } catch (Exception e) {}
                }
            } else {
                score.setVisibility(View.GONE);
            }

            // Для клика создаём объект Course из данных userCourse
            cardView.setOnClickListener(v -> {
                Course course = new Course();
                course.setId(userCourse.getCourseId());
                course.setTitle(userCourse.getTitle());
                course.setDescription(userCourse.getDescription());
                course.setImageUrl(userCourse.getImageUrl());
                course.setCourseNumber(userCourse.getCourseNumber());
                course.setDuration(userCourse.getDuration());
                course.setRating(userCourse.getRating());
                course.setCreatorFirstName(userCourse.getCreatorFirstName());
                course.setCreatorLastName(userCourse.getCreatorLastName());
                course.setCreatorCountry(userCourse.getCreatorCountry());
                course.setCreatorUniversity(userCourse.getCreatorUniversity());
                course.setCreatorType(userCourse.getCreatorType());

                if (listener != null) {
                    listener.onCourseClick(course);
                }
            });
        }
    }
}