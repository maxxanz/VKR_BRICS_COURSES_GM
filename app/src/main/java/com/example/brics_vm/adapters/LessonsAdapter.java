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

        if (completedLessonIds != null && completedLessonIds.contains(lesson.getId())) {
            holder.checkIcon.setVisibility(View.VISIBLE);
            holder.lessonIcon.setImageResource(R.drawable.ic_check_circle);
        } else {
            holder.checkIcon.setVisibility(View.GONE);
            holder.lessonIcon.setImageResource(R.drawable.ic_play_circle);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LessonDetailActivity.class);
            intent.putExtra(LessonDetailActivity.EXTRA_LESSON, lesson);
            intent.putExtra(LessonDetailActivity.EXTRA_COURSE_ID, courseId);
            ((AppCompatActivity) context).startActivityForResult(intent, 100); // 100 - requestCode
        });
    }

    @Override
    public int getItemCount() {
        return lessons != null ? lessons.size() : 0;
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        ImageView lessonIcon, checkIcon;
        TextView title, duration;

        LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonIcon = itemView.findViewById(R.id.lesson_icon);
            checkIcon = itemView.findViewById(R.id.check_icon);
            title = itemView.findViewById(R.id.lesson_title);
            duration = itemView.findViewById(R.id.lesson_duration);
        }
    }
}