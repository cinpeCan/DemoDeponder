package com.cinpe.deponder;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Dimension;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.util.Pair;

import com.cinpe.deponder.option.PlanetOption;
import com.cinpe.deponder.option.RootOption;

import org.reactivestreams.Publisher;

import java.util.Collection;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;

/**
 * @Description: 描述
 * @Author: Cinpe
 * @E-Mail: cinpeCan@outlook.com
 * @CreateDate: 2022/1/5
 * @Version: 0.01
 */
public class DeponderHelper {

    public static final String TAG = "DeponderHelper";

    /**
     * 空间初始化缩放值(当行星过多或过少时,可能进行缩放)
     */
    public final static float DEFAULT_INIT_SCALE = 1f;

    /**
     * 空间最大缩放值(在初始化缩放值基础上.)
     */
    public final static float DEFAULT_MAX_SCALE = 5f;

    /**
     * 空间最小缩放值(在初始化缩放值基础上.)
     */
    public final static float DEFAULT_MIN_SCALE = 0.2f;

    /**
     * 空间中以太密度.
     */
    public final static float DEFAULT_ROOT_DENSITY = 0.0006f;

    /**
     * 默认planet质量
     */
    public final static float DEFAULT_QUALITY_PROPERTY = 2.293f;

    /**
     * 默认的直径PX [动态]
     */
    public final static int DEFAULT_FRONTAL_AREA_PROPERTY = -1;

    /**
     * root的弹性系数.
     */
    public final static float DEFAULT_ROOT_ELASTICITY_COEFFICIENT = 7.293f;

    /**
     * planet的弹性系数.
     */
    public final static float DEFAULT_ELASTICITY_COEFFICIENT = 5.293f;

    /**
     * rubber的弹性系数.
     */
    public final static float DEFAULT_RUBBER_ELASTICITY_COEFFICIENT = 1.293f;

    /**
     * 四壁内压的感应距离.(px)
     */
    public final static int DEFAULT_Internal_Pressure = 300;

    /**
     * planet的感应距离.(px)
     */
    public final static int DEFAULT_PLANET_INTERNAL_PRESSURE = 220;

    /**
     * rubber的自然长度.(px)
     */
    public final static int DEFAULT_RUBBER_NATURAL_LENGTH = 600;

    /**
     * dp->px
     */
    public static @Px
    int dp2px(@NonNull Context context, @Dimension int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * hitRect
     */
    public static @NonNull
    Rect hitRect(@NonNull View v) {
        Rect rect = new Rect();
        v.getHitRect(rect);
        return rect;
    }

    /**
     * hitRectF
     */
    public static @NonNull
    RectF hitRectF(@NonNull View v) {
        Rect rect = new Rect();
        v.getHitRect(rect);
        return new RectF(rect);
    }

    /**
     * getValues
     */
    public static @NonNull
    float[] values(@NonNull Matrix matrix) {
        float[] floats = new float[9];
        matrix.getValues(floats);
        return floats;
    }

    /**
     * centerPointF
     */
    public static @NonNull
    PointF centerPointF(PointF s, PointF e) {
        return new PointF((s.x + e.x) / 2, (s.y + e.y) / 2);
    }

    /**
     * 矩形长宽获取斜边长.
     */
    public static float pythagorean(float dx, float dy) {
        return Math.round(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)));
    }

    /**
     * 获取矩阵差.
     */
    public static Matrix matrixDiff(Matrix sMatrix, Matrix eMatrix) {
        final Matrix invert = new Matrix();
        sMatrix.invert(invert);
        Matrix matrix = new Matrix();
        boolean concat = matrix.setConcat(invert, eMatrix);
        if (concat) {
            return matrix;
        } else {
            throw new IllegalStateException("IllegalState Matrix: [sMatrix] = " + sMatrix + " [eMatrix] = " + eMatrix);
        }

    }

    /**
     * 距离->形变距离.
     * 斥力方向为正
     *
     * @param distance 实际距离.
     */
    public static float dDistance(float distance, @FloatRange(from = 0) float internalPressure) {
        if (distance == 0) {
            //距离为0, 没有力的方向. 合力为0.
            return 0;
        } else if (distance > 0) {
            return internalPressure - distance;
        } else {
            return -internalPressure - distance;
        }
    }

    /**
     * 形变量->计算力
     *
     * @param difDistance 形变距离.
     */
    public static PointF calculate(@NonNull PointF difDistance, float elasticity_coefficient) {
        return new PointF(difDistance.x * elasticity_coefficient, difDistance.y * elasticity_coefficient);
    }

    /**
     * 距离计算加速度.
     *
     * @param difDistance 形变距离.
     */
    public static PointF calculate(@NonNull PointF difDistance, float elasticity_coefficient, float m) {
        final PointF newton = calculate(difDistance, elasticity_coefficient);
        return new PointF(newton.x / m, newton.y / m);
    }


    /**
     * 计算位移(矢量),
     *
     * @param speed        速度(px/ms)
     * @param acceleration 加速度(px/ms2)
     * @param dt           时间(ms)
     */
    public static PointF calculate(PointF speed, PointF acceleration, long dt) {
        float x = Math.round(speed.x * dt + .5f * acceleration.x * Math.pow(dt, 2)) * 0.000001f;
        float y = Math.round(speed.y * dt + .5f * acceleration.y * Math.pow(dt, 2)) * 0.000001f;
        return new PointF(x, y);
    }

    public static void bindDelegateRootTouch(@NonNull RootOption rootOption, @NonNull Collection<? extends PlanetOption> clt) {
        rootOption.itemView().post(() -> rootOption.itemView().setTouchDelegate(new DeponderDelegate(clt)));
//        todo 暂不考虑root的触控
//        todo rootOption.itemView().setOnTouchListener(new RootTouchHelper(rootOption.matrix()));
    }

    public static void bindPlanet(PlanetOption option) {
        option.itemView().setOnTouchListener(new TouchHelper(option));
    }

    /**
     * planet TouchHelper
     */
    static class TouchHelper extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

        public TouchHelper(@NonNull PlanetOption option) {
            this.option = option;
            mGestureDetector = new GestureDetector(option.itemView().getContext(), this);
            mScaleGestureDetector = new ScaleGestureDetector(option.itemView().getContext(), this);
        }

        private final PlanetOption option;
        private final GestureDetector mGestureDetector;
        private final ScaleGestureDetector mScaleGestureDetector;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Log.i(TAG, "进入PlanetOption.onTouch()" + event.getAction());

            if (!v.isEnabled()) {
                return false;
            }
            PointF point = new PointF(event.getX(), event.getY());
            Rect rect = new Rect();
            v.getHitRect(rect);

            RectF rectF = new RectF(rect);

            Matrix temp = new Matrix();

            float[] floats = new float[9];
            this.option.matrix().getValues(floats);

            rectF.offset(floats[Matrix.MTRANS_X], floats[Matrix.MTRANS_Y]);

            temp.postScale(floats[Matrix.MSCALE_X], floats[Matrix.MSCALE_Y], rectF.left, rectF.top);

            temp.mapRect(rectF);

            if (!rectF.contains(point.x, point.y)) {
                Log.i(TAG, "不包含, 不消费" + rectF + "," + this.option.matrix() + "," + point);
                return false;
            }
            Log.i(TAG, "包含, 进行消费" + rectF + "," + this.option.matrix() + "," + point);

            boolean retVal = mScaleGestureDetector.onTouchEvent(event);
            retVal = mGestureDetector.onTouchEvent(event) || retVal;

            return retVal;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            this.option.matrix().postScale(detector.getScaleFactor(),
                    detector.getScaleFactor(),
                    detector.getFocusX(),
                    detector.getFocusY());

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            //null
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i(TAG, "[planet]onSingleTapUp() called with: e = [" + e + "]");
            return this.option.itemView().performClick();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (e1 != null) {
                this.option.matrix().postTranslate(-distanceX, -distanceY);
            }

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }


    }

    /**
     * root TouchHelper
     */
    static class RootTouchHelper extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

        public RootTouchHelper(RootOption option) {
            this.option = option;
            mGestureDetector = new GestureDetector(option.itemView().getContext(), this);
            mScaleGestureDetector = new ScaleGestureDetector(option.itemView().getContext(), this);
        }

        private final RootOption option;

        private final GestureDetector mGestureDetector;
        private final ScaleGestureDetector mScaleGestureDetector;

        @Override
        public boolean onTouch(View v, MotionEvent event) {


            if (!v.isEnabled()) {
                return false;
            }

            boolean retVal = mScaleGestureDetector.onTouchEvent(event);
            retVal = mGestureDetector.onTouchEvent(event) || retVal;

            return retVal;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            option.matrix().postScale(detector.getScaleFactor(),
                    detector.getScaleFactor(),
                    detector.getFocusX(),
                    detector.getFocusY());

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
//            null
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return this.option.itemView().performClick();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            this.option.matrix().postTranslate(-distanceX, -distanceY);

//            v.scrollBy(Math.round(distanceX), Math.round(distanceY));
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    public static <O> CombinationsPair<O, Pair<O, O>> combinationsPair() {
        return new CombinationsPair<O, Pair<O, O>>() {
            @NonNull
            @Override
            protected BiFunction<O, O, Pair<O, O>> biFunction() {
                return Pair::create;
            }
        };
    }

    public static <O, P> FlowableCombinationsPair<O, P> flowableCombinationsPair(@NonNull BiFunction<O, O, P> biFunction) {
        return new FlowableCombinationsPair<O, P>() {
            @NonNull
            @Override
            protected BiFunction<O, O, P> biFunction() {
                return biFunction;
            }
        };
    }

    public static <O, P> FlowableTransformer<O, P> flowableCombinations(FlowableTransformer<O, O> UpstreamTransformer, FlowableTransformer<O, P> transformer) {
        return upstream -> upstream.compose(UpstreamTransformer).compose(transformer);
    }

    public static <O, P> CombinationsPair<O, P> combinationsPair(@NonNull BiFunction<O, O, P> biFunction) {
        return new CombinationsPair<O, P>() {
            @NonNull
            @Override
            protected BiFunction<O, O, P> biFunction() {
                return biFunction;
            }
        };
    }


    private static abstract class CombinationsPair<O, P> implements ObservableTransformer<O, P> {

        private int i = 0;

        @Override
        public @NonNull
        final ObservableSource<P> apply(@NonNull Observable<O> upstream) {
            return upstream
                    //todo 绘制po.
                    .doOnNext(o -> System.out.println("upstream.doOnNext:[评估四边压力]" + o))
                    .doAfterNext(o -> System.out.println("upstream.doAfterNext:[绘制po]" + o))
//                    .doFinally(() -> System.out.println("upstream.doFinally"))
                    .flatMap(o -> upstream.skip(++i)
//                            .doOnNext(e -> System.out.println("doOnNext o:" + o + ",e:" + e))
//                            .doAfterNext(e -> System.out.println("doAfterNext o:" + o + ",e:" + e))
                            //用来处理o.
//                            .doFinally(() -> System.out.println("doFinally:" + o))
                            , biFunction());
        }

        abstract protected @NonNull
        BiFunction<O, O, P> biFunction();

    }


    private static abstract class FlowableCombinationsPair<O, P> implements FlowableTransformer<O, P> {

        private int i = 0;

        @Override
        public @NonNull
        final Publisher<P> apply(@NonNull Flowable<O> upstream) {
            return upstream
                    //todo 绘制po.
                    .doOnNext(o -> Log.i(TAG, "看看i:" + i))
                    .doAfterNext(o -> Log.i(TAG, "看看after-i:" + i))
//                    .doFinally(() -> System.out.println("upstream.doFinally"))
                    .flatMap(o -> upstream.skip(++i)
//                            .doOnNext(e -> System.out.println("doOnNext o:" + o + ",e:" + e))
//                            .doAfterNext(e -> System.out.println("doAfterNext o:" + o + ",e:" + e))
                            //用来处理o.
//                            .doFinally(() -> System.out.println("doFinally:" + o))
                            , biFunction());
        }

        abstract protected @NonNull
        BiFunction<O, O, P> biFunction();

    }
}
