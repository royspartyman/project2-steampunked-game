// Generated code from Butter Knife. Do not modify!
package edu.msu.perrym23.project1;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SplashScreenActivity_ViewBinding<T extends SplashScreenActivity> implements Unbinder {
  protected T target;

  @UiThread
  public SplashScreenActivity_ViewBinding(T target, View source) {
    this.target = target;

    target.punpkinOneIV = Utils.findRequiredViewAsType(source, R.id.pumpkin_one, "field 'punpkinOneIV'", ImageView.class);
    target.punpkinTwoIV = Utils.findRequiredViewAsType(source, R.id.pumpkin_two, "field 'punpkinTwoIV'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.punpkinOneIV = null;
    target.punpkinTwoIV = null;

    this.target = null;
  }
}
