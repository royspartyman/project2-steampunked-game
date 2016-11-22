// Generated code from Butter Knife. Do not modify!
package edu.msu.perrym23.project1;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LoginUserActivity_ViewBinding<T extends LoginUserActivity> implements Unbinder {
  protected T target;

  private View view2131492998;

  private View view2131492999;

  @UiThread
  public LoginUserActivity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.login, "field 'loginButton' and method 'onLoginClick'");
    target.loginButton = Utils.castView(view, R.id.login, "field 'loginButton'", Button.class);
    view2131492998 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onLoginClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.new_user, "field 'newUserButton' and method 'onNewUserClick'");
    target.newUserButton = Utils.castView(view, R.id.new_user, "field 'newUserButton'", Button.class);
    view2131492999 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onNewUserClick();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.loginButton = null;
    target.newUserButton = null;

    view2131492998.setOnClickListener(null);
    view2131492998 = null;
    view2131492999.setOnClickListener(null);
    view2131492999 = null;

    this.target = null;
  }
}
