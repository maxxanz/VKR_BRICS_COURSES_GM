package com.example.brics_vm.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.models.Lesson;
import com.example.brics_vm.ui.lessons.LessonDetailActivity;
import java.util.List;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    private List<Integer> completedLessonIds;
    private Context context;
    private int courseId;

    public LessonsAdapter(Context context, int courseId) {
        this.context = context;
        this.courseId = courseId;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    public void setCompletedLessons(List<Integer> completedLessonIds) {
        this.completedLessonIds = completedLessonIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.title.setText(lesson.getTitle());
        holder.duration.setText(lesson.getDuration() + " мин");

        // Проверяем, является ли урок BRICS уроком
        // Используем флаг в названии или отдельное поле
        boolean isBricsLesson = lesson.getTitle() != null &&
                (lesson.getTitle().contains("🌍") ||
                        lesson.getTitle().toLowerCase().contains("brics") ||
                        (position == lessons.size() - 1 && lessons.size() > 1)); // последний урок

        if (isBricsLesson) {
            // Показываем бейдж BRICS
            holder.bricsBadge.setVisibility(View.VISIBLE);
            // Меняем цвет карточки
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.colorAccent));
            // Меняем иконку
            holder.lessonIcon.setImageResource(R.drawable.human);
        } else {
            holder.bricsBadge.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.white));
            // Стандартная иконка для обычных уроков
            if (completedLessonIds != null && completedLessonIds.contains(lesson.getId())) {
                holder.lessonIcon.setImageResource(R.drawable.ic_check_circle);
            } else {
                holder.lessonIcon.setImageResource(R.drawable.ic_play_circle);
            }
        }

        if (completedLessonIds != null && completedLessonIds.contains(lesson.getId())) {
            holder.checkIcon.setVisibility(View.VISIBLE);
            if (!isBricsLesson) {
                holder.lessonIcon.setImageResource(R.drawable.ic_check_circle);
            }
        } else {
            holder.checkIcon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(lesson);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessons != null ? lessons.size() : 0;
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        ImageView lessonIcon, checkIcon;
        TextView title, duration;
        TextView bricsBadge;
        CardView cardView;

        LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonIcon = itemView.findViewById(R.id.lesson_icon);
            checkIcon = itemView.findViewById(R.id.check_icon);
            title = itemView.findViewById(R.id.lesson_title);
            duration = itemView.findViewById(R.id.lesson_duration);
            bricsBadge = itemView.findViewById(R.id.brics_badge);
            cardView = (CardView) itemView;
        }
    }
    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Lesson lesson);
    }
}