package com.heizi.jtshop.block.maidan;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;
import com.heizi.mycommon.utils.ImageFactory;

public class ActivityImageViewShow extends BaseSwipeBackCompatActivity implements OnClickListener {

    private ImageView imageView;
    String url = "";

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_imageviewshow;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String url = getIntent().getStringExtra("url");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_title.setText("图片详情");
        findViewById(R.id.btn_back).setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setOnTouchListener(new MulitPointTouchListener());
        ImageFactory.displayImage(url, imageView, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                this.finish();
                break;

            default:
                break;
        }
    }

    float b_scaling = 1f;

    public class MulitPointTouchListener implements OnTouchListener {
        float mvx = 0f;
        float mvy = 0f;
        float x1 = 0f;
        float x2 = 0f;
        float y1 = 0f;
        float y2 = 0f;
        float xmax = 800f;
        float ymax = 600f;
        Matrix matrix = new Matrix();
        Matrix savedMatrix = new Matrix();
        static final int NONE = 0;
        static final int DRAG = 1;
        static final int ZOOM = 2;
        int mode = NONE;
        PointF start = new PointF();
        PointF mid = new PointF();
        float oldDist = 1f;
        float a_scaling = 1f;
        long actionDownTime = 0;
        long actionUpTime = 0;
        MotionEvent lastEvent = null;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView view = (ImageView) v;
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    matrix.set(view.getImageMatrix());
                    // savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    actionDownTime = System.currentTimeMillis();
                    lastEvent = event;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        // savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                /*if (System.currentTimeMillis() - actionDownTime < 200) {
                    if (groupsNum == 0) {
						if (RuiKlasseApplication.user.getRole() != 2) {// 同步按钮默认是不可见的，如果当前用户是老师，则显示同步按钮

						} else {
							replayCommentEvent();
						}
					} else {
						replayCommentEvent();
					}
					Log.i(TAG, "action_up");
				}*/
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        // matrix.set(savedMatrix);
                        mvx = event.getX() - start.x;
                        mvy = event.getY() - start.y;
                        if (mvx > 0) {
                            if (x1 < mvx) {
                                mvx = x1;
                            }
                        }
                        if (mvx < 0) {
                            if (mvx < x2) {
                                mvx = x2;
                            }
                        }
                        if (mvy > 0) {
                            if (y1 < mvy) {
                                mvy = y1;
                            }
                        }
                        if (mvy < 0) {
                            if (mvy < y2) {
                                mvy = y2;
                            }
                        }
                        x1 = x1 - mvx;
                        x2 = x2 - mvx;
                        y1 = y1 - mvy;
                        y2 = y2 - mvy;
                        matrix.set(view.getImageMatrix());
                        matrix.postTranslate(mvx, mvy);
                        start.set(event.getX(), event.getY());
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        // if (newDist > 10f) {
                        // matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        a_scaling = scale * b_scaling;
                        if (a_scaling >= 5f) {
                            scale = 5f / b_scaling;
                            b_scaling = 5f;
                        } else if (a_scaling <= 1f) {
                            scale = 1f / b_scaling;
                            b_scaling = 1f;
                        } else {
                            b_scaling = a_scaling;
                        }
                        xmax = xmax * scale;
                        ymax = ymax * scale;
                        x1 = (x1 + mid.x) * scale - mid.x;
                        x2 = (mid.x - 800f + x2) * scale + 800f - mid.x;
                        y1 = (y1 + mid.y) * scale - mid.y;
                        y2 = (mid.y - 600f + y2) * scale + 600f - mid.y;
                        if (Math.abs(scale - 1) > 0.0001) {
                            if (x1 < 0) {
                                mid.x = mid.x - x1 / (scale - 1);
                                x1 = 0;
                                x2 = -xmax + x1 + 800f;
                            }
                            if (y1 < 0) {
                                mid.y = mid.y - y1 / (scale - 1);
                                y1 = 0;
                                y2 = -ymax + y1 + 600f;
                            }
                            if (x2 > 0) {
                                mid.x = mid.x - x2 / (scale - 1);
                                x2 = 0;
                                x1 = xmax + x2 - 800f;
                            }
                            if (y2 > 0) {
                                mid.y = mid.y - y2 / (scale - 1);
                                y2 = 0;
                                y1 = ymax + y2 - 600f;
                            }
                        }
                        matrix.set(view.getImageMatrix());
                        matrix.postScale(scale, scale, mid.x, mid.y);
                        oldDist = newDist;
                    }
                    break;
            }
            view.setImageMatrix(matrix);
            return true;
        }

        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt((x * x + y * y));
        }

        private void midPoint(PointF point, MotionEvent event) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }
    }
}

