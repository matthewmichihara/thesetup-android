package com.fourpool.thesetup;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.InjectView;
import java.util.Collections;
import java.util.List;
import rx.functions.Action1;

import static butterknife.ButterKnife.inject;

public class InterviewsAdapter extends RecyclerView.Adapter<InterviewsAdapter.ViewHolder>
    implements InterviewView.Listener, Action1<List<Interview>> {
  private final Context context;
  private List<Interview> interviews = Collections.emptyList();
  private Listener listener;

  public interface Listener {
    void onInterviewClick(Interview interview, Pair<View, String>... sharedElements);
  }

  public InterviewsAdapter(Context context) {
    this.context = context;
  }

  @Override public void call(List<Interview> interviews) {
    this.interviews = interviews;
    notifyDataSetChanged();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(context).inflate(R.layout.list_item_interview, viewGroup, false);
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
    if (listener != null) listener.onInterviewClick(interview, sharedElements);
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.interview) InterviewView interviewView;

    public ViewHolder(View view) {
      super(view);
      inject(this, view);
    }
  }
}
