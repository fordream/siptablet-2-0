package z.lib.base;


public class BaseAnimationUtils {
	public interface AnimationAction {

		public void onAnimationEnd();

	}

//	public static void openActivityRightToLeft(Context context, View mMain, final AnimationAction animationAction) {
//		if (mMain == null)
//			return;
//		Animation animation = AnimationUtils.loadAnimation(context, R.anim.a_nothing);
//		animation.setAnimationListener(new AnimationListener() {
//
//			@Override
//			public void onAnimationStart(Animation animation) {
//
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//
//			}
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				if (animationAction != null)
//					animationAction.onAnimationEnd();
//			}
//		});
//		mMain.startAnimation(animation);
//	}

	// public static void faceoutView(Context context, View mMain, final
	// AnimationAction animationAction) {
	// if (mMain == null)
	// return;
	// Animation animation = AnimationUtils.loadAnimation(context,
	// R.anim.fadeout);
	// animation.setAnimationListener(new AnimationListener() {
	//
	// @Override
	// public void onAnimationStart(Animation animation) {
	//
	// }
	//
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	//
	// }
	//
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// if (animationAction != null)
	// animationAction.onAnimationEnd();
	// }
	// });
	// mMain.startAnimation(animation);
	// }

	// public static void faceinView(Context context, View mMain, final
	// AnimationAction animationAction) {
	// Animation animation = AnimationUtils.loadAnimation(context,
	// R.anim.fadein);
	// animation.setFillAfter(false);
	// animation.setAnimationListener(new AnimationListener() {
	//
	// @Override
	// public void onAnimationStart(Animation animation) {
	//
	// }
	//
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	//
	// }
	//
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// if (animationAction != null)
	// animationAction.onAnimationEnd();
	// }
	// });
	// mMain.startAnimation(animation);
	// }

//	public static void openMenuActivity(Context context, View mMain, final AnimationAction animationAction) {
//		if (mMain == null)
//			return;
//		Animation animation = AnimationUtils.loadAnimation(context, R.anim.a_menu_right_in);
//		AnimationListener animationListener = new AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {
//
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//
//			}
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				if (animationAction != null)
//					animationAction.onAnimationEnd();
//			}
//		};
//		animation.setAnimationListener(animationListener);
//
//		mMain.startAnimation(animation);
//
//	}
//
//	public static void closeMenuActivity(Context context, View mMain, final AnimationAction animationAction) {
//		if (mMain == null)
//			return;
//
//		Animation animation = AnimationUtils.loadAnimation(context, R.anim.a_menu_right_out);
//		AnimationListener animationListener = new AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {
//
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//
//			}
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				if (animationAction != null)
//					animationAction.onAnimationEnd();
//			}
//		};
//		animation.setAnimationListener(animationListener);
//
//		mMain.startAnimation(animation);
//	}
}