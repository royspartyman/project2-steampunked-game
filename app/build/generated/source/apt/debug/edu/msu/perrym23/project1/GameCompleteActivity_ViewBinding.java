// Generated code from Butter Knife. Do not modify!
package edu.msu.perrym23.project1;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GameCompleteActivity_ViewBinding<T extends GameCompleteActivity> implements Unbinder {
  protected T target;

  private View view2131492989;

  @UiThread
  public GameCompleteActivity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.gameOverTextTV = Utils.findRequiredViewAsType(source, R.id.game_over_text, "field 'gameOverTextTV'", TextView.class);
    target.loserTV = Utils.findRequiredViewAsType(source, R.id.loser, "field 'loserTV'", TextView.class);
    target.winnerTV = Utils.findRequiredViewAsType(source, R.id.winner, "field 'winnerTV'", TextView.class);
    view = Utils.findRequiredView(source, R.id.back_to_menu, "field 'back_to_menu' and method 'onBackToMenu'");
    target.back_to_menu = Utils.castView(view, R.id.back_to_menu, "field 'back_to_menu'", Button.class);
    view2131492989 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onBackToMenu();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.gameOverTextTV = null;
    target.loserTV = null;
    target.winnerTV = null;
    target.back_to_menu = null;

    view2131492989.setOnClickListener(null);
    view2131492989 = null;

    this.target = null;
  }
}
