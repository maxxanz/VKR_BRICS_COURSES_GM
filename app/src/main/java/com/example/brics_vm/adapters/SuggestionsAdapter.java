package com.example.brics_vm.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.models.Lesson;
import com.example.brics_vm.models.Suggestion;
import com.example.brics_vm.ui.lessons.LessonDetailActivity;
import java.util.List;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {

    private List<Suggestion> suggestions;
    private Context context;
    private OnSuggestionActionListener actionListener;

    public interface OnSuggestionActionListener {
        void onApprove(Suggestion suggestion);
        void onReject(Suggestion suggestion);
    }

    public void setOnSuggestionActionListener(OnSuggestionActionListener listener) {
        this.actionListener = listener;
    }

    public void setSuggestions(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Suggestion s = suggestions.get(position);

        // ПОКАЗЫВАЕМ НАЗВАНИЕ КУРСА
        String courseInfo = "📘 " + s.getCourseTitle();
        holder.courseTitle.setText(courseInfo);

        holder.title.setText(s.getTitle());
        holder.teacherInfo.setText("📝 от " + s.getTeacherName() + " (" + s.getCountry() + ")");

        // Показываем превью содержания (первые 100 символов)
        String contentPreview = s.getTextContent();
        if (contentPreview != null && contentPreview.length() > 100) {
            contentPreview = contentPreview.substring(0, 100) + "...";
        }
        holder.content.setText(contentPreview);

        if (s.getVideoUrl() != null && !s.getVideoUrl().isEmpty()) {
            holder.videoHint.setVisibility(View.VISIBLE);
        } else {
            holder.videoHint.setVisibility(View.GONE);
        }

        holder.approveBtn.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onApprove(s);
        });

        holder.rejectBtn.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onReject(s);
        });

        // Клик на карточку — открываем предпросмотр урока
        holder.cardView.setOnClickListener(v -> {
            Lesson previewLesson = new Lesson();
            previewLesson.setId(-1);
            previewLesson.setTitle(s.getTitle());
            previewLesson.setTextContent(s.getTextContent());
            previewLesson.setVideoUrl(s.getVideoUrl());
            previewLesson.setDuration(parseDuration(s.getDuration()));

            Intent intent = new Intent(context, LessonDetailActivity.class);
            intent.putExtra(LessonDetailActivity.EXTRA_LESSON, previewLesson);
            intent.putExtra(LessonDetailActivity.EXTRA_COURSE_ID, s.getCourseId());
            intent.putExtra("is_preview_mode", true);
            context.startActivity(intent);
        });
    }

    private int parseDuration(String durationStr) {
        try {
            return Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    @Override
    public int getItemCount() {
        return suggestions != null ? suggestions.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle;  // ← ДОБАВИТЬ
        TextView title, teacherInfo, content, videoHint;
        Button approveBtn, rejectBtn;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.suggestion_course_title);  // ← ДОБАВИТЬ
            title = itemView.findViewById(R.id.suggestion_title);
            teacherInfo = itemView.findViewById(R.id.suggestion_teacher);
            content = itemView.findViewById(R.id.suggestion_content);
            videoHint = itemView.findViewById(R.id.suggestion_video_hint);
            approveBtn = itemView.findViewById(R.id.approve_btn);
            rejectBtn = itemView.findViewById(R.id.reject_btn);
            cardView = (CardView) itemView;
        }
    }
}