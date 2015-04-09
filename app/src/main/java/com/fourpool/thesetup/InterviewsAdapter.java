package com.fourpool.thesetup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class InterviewsAdapter extends RecyclerView.Adapter<InterviewsAdapter.ViewHolder>
    implements InterviewView.Listener {
  private final Context context;
  private final List<Interview> interviews;
  private final Listener listener;

  public interface Listener {
    void onInterviewClick(Interview interview, Pair<View, String>... sharedElements);
  }

  public InterviewsAdapter(@NonNull Context context, @NonNull List<Interview> interviews,
      @NonNull Listener listener) {
    this.context = context;
    this.interviews = interviews;
    this.listener = listener;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    InterviewView v = new InterviewView(context);
    v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
    final Interview interview = interviews.get(i);
    viewHolder.interviewView.setInterview(interview);
    viewHolder.interviewView.setListener(this);
  }

  @Override public int getItemCount() {
    return interviews.size();
  }

  @Override public void onClick(Interview interview, Pair<View, String>... sharedElements) {
    listener.onInterviewClick(interview, sharedElements);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    public final InterviewView interviewView;

    public ViewHolder(InterviewView interviewView) {
      super(interviewView);

      this.interviewView = interviewView;
    }
  }
}
