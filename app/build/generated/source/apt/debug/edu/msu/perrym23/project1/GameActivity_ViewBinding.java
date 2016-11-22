// Generated code from Butter Knife. Do not modify!
package edu.msu.perrym23.project1;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GameActivity_ViewBinding<T extends GameActivity> implements Unbinder {
  protected T target;

  private View view2131492976;

  private View view2131492981;

  private View view2131492980;

  private View view2131492977;

  @UiThread
  public GameActivity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.currentPlayerTV = Utils.findRequiredViewAsType(source, R.id.currentPlayer, "field 'currentPlayerTV'", TextView.class);
    view = Utils.findRequiredView(source, R.id.install, "field 'installFAB' and method 'onInstallFABClick'");
    target.installFAB = Utils.castView(view, R.id.install, "field 'installFAB'", FloatingActionButton.class);
    view2131492976 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onInstallFABClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.open, "field 'openFAB' and method 'onOpenFABClick'");
    target.openFAB = Utils.castView(view, R.id.open, "field 'openFAB'", FloatingActionButton.class);
    view2131492981 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onOpenFABClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.discard, "field 'discardFAB' and method 'onDiscardFABClick'");
    target.discardFAB = Utils.castView(view, R.id.discard, "field 'discardFAB'", FloatingActionButton.class);
    view2131492980 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onDiscardFABClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.forfeit, "field 'forfeitFAB' and method 'onForfeitFABClick'");
    target.forfeitFAB = Utils.castView(view, R.id.forfeit, "field 'forfeitFAB'", FloatingActionButton.class);
    view2131492977 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onForfeitFABClick();
      }
    });
    target.batPlayerIV = Utils.findRequiredViewAsType(source, R.id.bat_player, "field 'batPlayerIV'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.currentPlayerTV = null;
    target.installFAB = null;
    target.openFAB = null;
    target.discardFAB = null;
    target.forfeitFAB = null;
    target.batPlayerIV = null;

    view2131492976.setOnClickListener(null);
    view2131492976 = null;
    view2131492981.setOnClickListener(null);
    view2131492981 = null;
    view2131492980.setOnClickListener(null);
    view2131492980 = null;
    view2131492977.setOnClickListener(null);
    view2131492977 = null;

    this.target = null;
  }
}
