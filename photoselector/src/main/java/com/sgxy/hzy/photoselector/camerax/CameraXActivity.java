package com.sgxy.hzy.photoselector.camerax;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCapture.OnImageSavedCallback;
import androidx.camera.core.ImageCapture.OutputFileResults;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.utils.Exif;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import com.sgxy.hzy.photoselector.R;
import com.sgxy.hzy.photoselector.base.BaseActivity;
import com.sgxy.hzy.photoselector.util.ImageUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@TargetApi(21)
public class CameraXActivity extends BaseActivity {

	private static final String TAG = "CameraXActivity";
	private static final float RATIO_4_3 = 4.0f / 3.0f;
	private static final float RATIO_16_9 = 16.0f / 9.0f;

	/**
	 * 启动前置摄像头拍摄
	 */
	public static Intent getStartIntent(final Activity activity, final String path) {
		Intent intent = new Intent(activity, CameraXActivity.class);
		intent.putExtra("path", path);
		return intent;
	}

	private String path = "";
	private ViewFlipper flipper;
	private PreviewView preview;
	private ImageView image;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camerax);
		path = getIntent().getStringExtra("path");
		if (TextUtils.isEmpty(path)) {
			finish();
			return;
		}
		flipper = findViewById(R.id.flipper);
		image = findViewById(R.id.image);
		preview = findViewById(R.id.preview);
		preview.post(new Runnable() {
			@Override
			public void run() {
				onViewReady();
			}
		});
		//初始化
		orientation = new OrientationEventListener(this) {

			@Override
			public void onOrientationChanged(int orientation) {
				Log.d(TAG, "onOrientationChanged():orientation = " + orientation);
				screenRotation = orientation;
			}
		};
	}

	private OrientationEventListener orientation = null;

	@Override
	protected void onResume() {
		super.onResume();
		if (null != orientation && orientation.canDetectOrientation()) {
			orientation.enable();
		}
	}

	private int screenRotation = 0;

	@Override
	protected void onPause() {
		super.onPause();
		if (null != orientation && orientation.canDetectOrientation()) {
			orientation.disable();
		}
	}

	private LifecycleOwner getLifecycleOwner() {
		return this;
	}

	/**
	 * 选择合适aspect ratio
	 */
	private int chooseAspectRatio(int width, int height) {
		float ratio = 1.0f * Math.max(width, height) / Math.min(width, height);
		if (Math.abs(ratio - RATIO_4_3) <= Math.abs((ratio - RATIO_16_9))) {
			return AspectRatio.RATIO_4_3;
		}
		return AspectRatio.RATIO_16_9;
	}

	/**
	 * 当view初始化完成之后
	 */
	private void onViewReady() {
		final int aspectRatio = chooseAspectRatio(preview.getWidth(), preview.getHeight());
		final int rotation = preview.getDisplay().getRotation();
		final ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
		future.addListener(new Runnable() {
			@Override
			public void run() {
				try {
					ProcessCameraProvider provider = future.get();

					Preview casePreview = new Preview.Builder()
						.setTargetAspectRatio(aspectRatio)
						.setTargetRotation(rotation)
						.build();
					casePreview.setSurfaceProvider(preview.createSurfaceProvider(null));

					final ImageCapture caseCapture = new ImageCapture.Builder()
						.setFlashMode(ImageCapture.FLASH_MODE_AUTO)
						.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
						.setTargetAspectRatio(aspectRatio)
						.setTargetRotation(rotation)
						.build();

					provider.unbindAll();

					CameraSelector cameraSelector = new CameraSelector.Builder()
						.requireLensFacing(CameraSelector.LENS_FACING_FRONT)
						.build();

					provider.bindToLifecycle(getLifecycleOwner(), cameraSelector, casePreview, caseCapture);
					//设置点击事件
					findViewById(R.id.shot).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (ImageUtil.isFastDoubleClick(v)) {
								return;
							}
							if (flipper.getDisplayedChild() != 0) {
								return;
							}
							takePhoto(caseCapture, v.getContext());
						}
					});
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
		}, ContextCompat.getMainExecutor(this));
		//设置点击事件
		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				File file = new File(path);
				if (file.exists()) {
					file.delete();
				}
				if (isFinishing()) {
					return;
				}
				finish();
			}
		});
		findViewById(R.id.reshot).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flipper.getDisplayedChild() != 1) {
					return;
				}
				File file = new File(path);
				if (file.exists()) {
					file.delete();
				}
				flipper.setInAnimation(v.getContext(), R.anim.left_in);
				flipper.setOutAnimation(v.getContext(), R.anim.right_out);
				flipper.showPrevious();
				image.setImageResource(R.color.black);
			}
		});
		findViewById(R.id.use).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isFinishing()) {
					return;
				}
				finish();
			}
		});
	}

	private AtomicBoolean isWorking = new AtomicBoolean(false);
	private ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * 拍照
	 */
	private synchronized void takePhoto(final ImageCapture capture, final Context context) {
		if (isFinishing() || isDestroyed() || executor.isShutdown()) {
			return;
		}
		if (!isWorking.compareAndSet(false, true)) {
			return;
		}
		final ImageCapture.Metadata metadata = new ImageCapture.Metadata();
		metadata.setReversedHorizontal(true);

		final File file = new File(path);
		final ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions
			.Builder(file)
			.setMetadata(metadata)
			.build();

		final int screen = screenRotation;

		capture.takePicture(options, executor, new OnImageSavedCallback() {
			@Override
			public void onImageSaved(@NonNull OutputFileResults outputFileResults) {
				Log.d(TAG, "take picture done." + outputFileResults);
				if (isFinishing() || isDestroyed()) {
					if (!executor.isShutdown()) {
						executor.shutdown();
					}
					isWorking.set(false);
					return;
				}
				if (file.exists() && file.length() > 0) {
					//切换预览
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							image.setImageResource(R.color.black);
							flipper.setInAnimation(context, R.anim.right_in);
							flipper.setOutAnimation(context, R.anim.left_out);
							flipper.showNext();
						}
					});
					//前置摄像镜像翻转，相机旋转矫正，屏幕旋转矫正，尺寸比例缩放
					reverseAndResizeBitmapNow(file, screen);
				} else {
					Log.e(TAG, "file is not exist." + path);
				}
				isWorking.set(false);
			}

			@Override
			public void onError(@NonNull ImageCaptureException exception) {
				Log.e(TAG, exception.toString());
				isWorking.set(false);
			}
		});
	}

	/**
	 * 获取exit旋转角度
	 */
	@SuppressLint("RestrictedApi")
	private int getRawRotation(final File file) {
		if (file == null || !file.exists()) {
			return 0;
		}
		try {
			Exif exif = Exif.createFromFile(file);
			return exif.getRotation();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		return 0;
	}

	/**
	 * 屏幕方向矫正
	 */
	private int restrictScreenRotation(int screen) {
		Log.d(TAG, "restrictScreenRotation:snapshot = " + screen);
		int OFFSET = 10;
		if (Math.abs(screen) <= OFFSET) {
			return 0;
		} else if (Math.abs(screen - 90) <= OFFSET) {
			return 90;
		} else if (Math.abs(screen - 180) <= OFFSET) {
			return 180;
		} else if (Math.abs(screen - 270) <= OFFSET) {
			return 270;
		}
		if (screen > 270 + OFFSET) {
			return 0;
		} else if (screen > 180 + OFFSET) {
			return 180;
		} else if (screen > 90 + OFFSET) {
			return 90;
		} else {
			return 0;
		}
	}

	private static final int FIT = 1024 * 2;

	/**
	 * 获取合适的比例
	 */
	private int getScaleToFit(int width, int height) {
		int scale = 1;
		int size = Math.min(width, height);
		while ((size / scale) >= FIT) {
			scale = scale * 2;
		}
		return scale;
	}

	/**
	 * 照相图片水平翻转
	 */
	private void reverseAndResizeBitmapNow(final File file, int screen) {
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		Matrix matrix = new Matrix();
		int scale = getScaleToFit(bitmap.getWidth(), bitmap.getHeight());
		matrix.postScale(-1.0f / scale, 1.0f / scale);
		matrix.postRotate(getRawRotation(file) + restrictScreenRotation(screen));
		Bitmap reverse = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (reverse != bitmap && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(file.getAbsoluteFile());
			reverse.compress(CompressFormat.JPEG, 100, stream);
			stream.flush();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
			if (!reverse.isRecycled()) {
				reverse.recycle();
			}
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				image.setImageURI(Uri.fromFile(file));
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!isWorking.get()) {
			if (!executor.isShutdown()) {
				executor.shutdown();
			}
		}
		if (null != orientation) {
			orientation = null;
		}
	}
}
