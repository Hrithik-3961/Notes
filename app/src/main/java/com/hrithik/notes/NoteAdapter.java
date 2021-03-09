package com.hrithik.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {

    public OnLongClickListener longClickListener;
    public OnClickListener onClickListener;

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription());
        }
    };

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);

        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = getItem(position);

        holder.title.setText(currentNote.getTitle());
        holder.description.setText(currentNote.getDescription());
    }

    public Note getNoteAt(int position) {
        return getItem(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView description;

        public NoteHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);

            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (longClickListener != null && position != RecyclerView.NO_POSITION)
                        longClickListener.onLongClick(getItem(position), itemView, position);
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                        onClickListener.onItemClick(getItem(position), itemView, position);
                    }

                }
            });
        }
    }

    public interface OnLongClickListener {
        void onLongClick(Note note, View item, int position);
    }

    public void setLongClickListener(OnLongClickListener listener) {
        longClickListener = listener;
    }

    public interface OnClickListener {
        void onItemClick(Note note, View item, int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }
}
