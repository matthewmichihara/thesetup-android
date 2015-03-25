package com.fourpool.thesetup;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Gross class that uses regexes to parse a very limited set of markdown */
@Singleton public class Spanner {
  private static final Pattern GOD_PATTERN =
      Pattern.compile("(#### (.+?\\n\\n))|(\\[(.*?)\\](\\[(.*?)\\]|\\((.+?) (.*?)\\)))");

  @Inject public Spanner() {
  }

  public interface Listener {
    void onLinkClick(String url);

    void onItemClick(String slug);
  }

  public ProcessedContents process(final Context context, final String contents,
      final Listener listener) {
    List<SpanInfo> spanInfos = new ArrayList<>();
    String newContents = contents;

    int totalOffset = 0;

    Matcher matcher = GOD_PATTERN.matcher(contents);
    while (matcher.find()) {
      final String matchedHeader = matcher.group(1);
      final String matchedHeaderContents = matcher.group(2);
      final String matchedItem = matcher.group(3);
      final String matchedItemName = matcher.group(4);
      final String matchedLinkUrl = matcher.group(7);
      final String matchedItemSlug = matcher.group(6);

      if (matchedHeader != null) {
        int spanStart = newContents.indexOf(matchedHeader, matcher.start() + totalOffset);
        int spanEnd = spanStart + matchedHeaderContents.length() - 2;

        SpanInfo textStyleSpan =
            new SpanInfo(new TextAppearanceSpan(context, R.style.HeaderTextAppearance), spanStart, spanEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpanInfo colorSpan =
            new SpanInfo(new ForegroundColorSpan(context.getResources().getColor(R.color.pink)),
                spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanInfos.add(textStyleSpan);
        //spanInfos.add(colorSpan);

        newContents = newContents.replaceFirst("#### .+?\\n\\n", matchedHeaderContents);

        int offset = matchedHeader.length() - matchedHeaderContents.length();
        totalOffset -= offset;
      } else if (matchedLinkUrl != null) {
        int spanStart = newContents.indexOf(matchedItem, matcher.start() + totalOffset);
        int spanEnd = spanStart + matchedItemName.length();

        ClickableSpan span = new ClickableSpan() {
          @Override public void onClick(View widget) {
            listener.onLinkClick(matchedLinkUrl);
          }
        };

        SpanInfo spanInfo =
            new SpanInfo(span, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanInfos.add(spanInfo);

        newContents = newContents.replaceFirst("(\\[(.*?)\\]\\((.*?) .*?\\))", matchedItemName);

        int offset = matchedItem.length() - matchedItemName.length();
        totalOffset -= offset;
      } else if (matchedItemSlug != null) {
        int spanStart = newContents.indexOf(matchedItem, matcher.start() + totalOffset);
        int spanEnd = spanStart + matchedItemName.length();

        ClickableSpan span = new ClickableSpan() {
          @Override public void onClick(View widget) {
            String slug = TextUtils.isEmpty(matchedItemSlug) ? matchedItemName : matchedItemSlug;
            listener.onItemClick(slug);
          }
        };

        SpanInfo spanInfo =
            new SpanInfo(span, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanInfos.add(spanInfo);

        newContents = newContents.replaceFirst("\\[.*?\\]\\[.*?\\]", matchedItemName);

        int offset = matchedItem.length() - matchedItemName.length();
        totalOffset -= offset;
      }
    }

    return new ProcessedContents(newContents, spanInfos);
  }

  public static class SpanInfo {
    public final Object span;
    public final int start;
    public final int end;
    public final int flags;

    public SpanInfo(Object span, int start, int end, int flags) {
      this.span = span;
      this.start = start;
      this.end = end;
      this.flags = flags;
    }
  }

  public static class ProcessedContents {
    public final String contents;
    public final List<SpanInfo> spanInfos;

    public ProcessedContents(String contents, List<SpanInfo> spanInfos) {
      this.contents = contents;
      this.spanInfos = spanInfos;
    }
  }
}
