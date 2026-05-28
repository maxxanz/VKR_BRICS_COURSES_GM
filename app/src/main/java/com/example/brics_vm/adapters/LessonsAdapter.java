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

        // ОПРЕДЕЛЯЕМ ТИП УРОКА
        boolean isBricsLesson = lesson.isBrics();  // Используем поле из БД

        // Для обратной совместимости (если поле ещё не заполнено)
        if (!isBricsLesson && lesson.getTitle() != null) {
            isBricsLesson = lesson.getTitle().contains("🌍") ||
                    lesson.getTitle().toLowerCase().contains("brics");
        }

        boolean isContribution = lesson.isContribution();

        if (isBricsLesson) {
            // BRICS урок
            holder.bricsBadge.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.colorAccent));
            holder.lessonIcon.setImageResource(R.drawable.human);
            holder.contributorInfo.setVisibility(View.GONE);  // скрываем информацию об авторе
            holder.checkIcon.setVisibility(View.GONE);

        } else if (isContribution) {
            // Дополнительный урок от преподавателя из другой страны
            holder.bricsBadge.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.colorContribution));

            if (completedLessonIds != null && completedLessonIds.contains(lesson.getId())) {
                holder.lessonIcon.setImageResource(R.drawable.ic_check_circle);
            } else {
                holder.lessonIcon.setImageResource(R.drawable.ic_play_circle);
            }

            // Показываем информацию об авторе
            holder.contributorInfo.setVisibility(View.VISIBLE);
            String contributorText = "🌍 Дополнительный урок от " +
                    lesson.getContributorFullName() +
                    " (" + lesson.getContributorCountry() + ")";
            holder.contributorInfo.setText(contributorText);
            holder.checkIcon.setVisibility(completedLessonIds != null &&
                    completedLessonIds.contains(lesson.getId()) ? View.VISIBLE : View.GONE);

        } else {
            // Обычный урок
            holder.bricsBadge.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.white));
            holder.contributorInfo.setVisibility(View.GONE);

            if (completedLessonIds != null && completedLessonIds.contains(lesson.getId())) {
                holder.lessonIcon.setImageResource(R.drawable.ic_check_circle);
                holder.checkIcon.setVisibility(View.VISIBLE);
            } else {
                holder.lessonIcon.setImageResource(R.drawable.ic_play_circle);
                holder.checkIcon.setVisibility(View.GONE);
            }
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
        TextView contributorInfo;  // ← ДОБАВИТЬ
        CardView cardView;

        LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonIcon = itemView.findViewById(R.id.lesson_icon);
            checkIcon = itemView.findViewById(R.id.check_icon);
            title = itemView.findViewById(R.id.lesson_title);
            duration = itemView.findViewById(R.id.lesson_duration);
            bricsBadge = itemView.findViewById(R.id.brics_badge);
            contributorInfo = itemView.findViewById(R.id.contributor_info);  // ← ДОБАВИТЬ
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