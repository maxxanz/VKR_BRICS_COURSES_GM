package com.example.brics_vm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.brics_vm.R;
import com.example.brics_vm.models.Course;
import java.util.List;
import android.content.Intent;
import com.example.brics_vm.ui.coursedetail.CourseDetailActivity;

public class MarketplaceAdapter extends RecyclerView.Adapter<MarketplaceAdapter.ViewHolder> {
    private List<Course> courses;

    public MarketplaceAdapter(List<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_marketplace, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);

        holder.itemTitle.setText(course.getTitle());
        holder.itemDescription.setText(course.getDescription());
        holder.itemRating.setText("★ " + course.getRatingDisplay());
        holder.itemLevel.setText(course.getCourseNumberDisplay());
        holder.itemDuration.setText(course.getDurationDisplay());

        // 🔥 УЛУЧШЕННАЯ ЗАГРУЗКА ИЗОБРАЖЕНИЙ 🔥
        String imageUrl = course.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Загружаем изображение по URL из БД
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)   // пока грузится
                    .error(R.drawable.ic_error)              // если ошибка загрузки
                    .centerCrop()
                    .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                    .into(holder.itemImage);
        } else {
            // Если URL нет - показываем заглушку
            holder.itemImage.setImageResource(R.drawable.ic_placeholder);
        }

        // Обработка клика
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), CourseDetailActivity.class);
            intent.putExtra(CourseDetailActivity.EXTRA_COURSE, course);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    public void updateCourses(List<Course> newCourses) {
        this.courses = newCourses;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemDescription;
        TextView itemRating;
        TextView itemLevel;
        TextView itemDuration;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDescription = itemView.findViewById(R.id.item_description);
            itemRating = itemView.findViewById(R.id.item_rating);
            itemLevel = itemView.findViewById(R.id.item_level);
            itemDuration = itemView.findViewById(R.id.item_duration);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}