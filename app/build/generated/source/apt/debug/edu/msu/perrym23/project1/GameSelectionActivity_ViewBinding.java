// Generated code from Butter Knife. Do not modify!
package edu.msu.perrym23.project1;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GameSelectionActivity_ViewBinding<T extends GameSelectionActivity> implements Unbinder {
  protected T target;

  private View view2131492991;

  private View view2131492993;

  private View view2131492995;

  private View view2131492996;

  private View view2131492997;

  @UiThread
  public GameSelectionActivity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.userOne, "field 'userOneIV' and method 'userOneIBClicked'");
    target.userOneIV = Utils.castView(view, R.id.userOne, "field 'userOneIV'", ImageView.class);
    view2131492991 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.userOneIBClicked();
      }
    });
    view = Utils.findRequiredView(source, R.id.userTwo, "field 'userTwoIV' and method 'userTwoIBClicked'");
    target.userTwoIV = Utils.castView(view, R.id.userTwo, "field 'userTwoIV'", ImageView.class);
    view2131492993 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.userTwoIBClicked();
      }
    });
    target.playerOneTV = Utils.findRequiredViewAsType(source, R.id.playerOne, "field 'playerOneTV'", TextView.class);
    target.playerTwoTV = Utils.findRequiredViewAsType(source, R.id.playerTwo, "field 'playerTwoTV'", TextView.class);
    view = Utils.findRequiredView(source, R.id.fivebyfive, "field 'fivebyfive' and method 'fiveByFiveClicked'");
    target.fivebyfive = Utils.castView(view, R.id.fivebyfive, "field 'fivebyfive'", Button.class);
    view2131492995 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.fiveByFiveClicked();
      }
    });
    view = Utils.findRequiredView(source, R.id.tenbyten, "field 'tenbyten' and method 'tenBytenClicked'");
    target.tenbyten = Utils.castView(view, R.id.tenbyten, "field 'tenbyten'", Button.class);
    view2131492996 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.tenBytenClicked();
      }
    });
    view = Utils.findRequiredView(source, R.id.twentybytwenty, "field 'twentybytwenty' and method 'twentyByTwentClicked'");
    target.twentybytwenty = Utils.castView(view, R.id.twentybytwenty, "field 'twentybytwenty'", Button.class);
    view2131492997 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.twentyByTwentClicked();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.userOneIV = null;
    target.userTwoIV = null;
    target.playerOneTV = null;
    target.playerTwoTV = null;
    target.fivebyfive = null;
    target.tenbyten = null;
    target.twentybytwenty = null;

    view2131492991.setOnClickListener(null);
    view2131492991 = null;
    view2131492993.setOnClickListener(null);
    view2131492993 = null;
    view2131492995.setOnClickListener(null);
    view2131492995 = null;
    view2131492996.setOnClickListener(null);
    view2131492996 = null;
    view2131492997.setOnClickListener(null);
    view2131492997 = null;

    this.target = null;
  }
}
