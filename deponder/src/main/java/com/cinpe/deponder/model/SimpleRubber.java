package com.cinpe.deponder.model;

import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cinpe.deponder.DeponderHelper;
import com.cinpe.deponder.NAnimator;
import com.cinpe.deponder.option.BaseOption;
import com.cinpe.deponder.option.RubberOption;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

/**
 * @Description: 描述
 * @Author: Cinpe
 * @E-Mail: cinpeCan@outlook.com
 * @CreateDate: 2022/1/6
 * @Version: 0.01
 */
@AutoValue
public abstract class SimpleRubber extends RubberOption {

    @NonNull
    @Override
    @AutoValue.CopyAnnotations
    public abstract View itemView();

    @NonNull
    @Override
    @AutoValue.CopyAnnotations
    public abstract String id();

    @NonNull
    @Override
    @AutoValue.CopyAnnotations
    public abstract String sId();

    @NonNull
    @Override
    @AutoValue.CopyAnnotations
    public abstract String eId();

    @NonNull
    @Override
    @AutoValue.CopyAnnotations
    public abstract NAnimator animator();

    @NonNull
    @Override
    @AutoValue.CopyAnnotations
    public abstract Matrix matrix();

    @Override
    public abstract float elasticityCoefficient();

    @Override
    public abstract int naturalLength();

    @Override
    public abstract ImmutableList<BaseOption> vArr();

    public static SimpleRubber create(View itemView, String sId, String eId, float elasticityCoefficient, int naturalLength) {
        return builder()
                .itemView(itemView)
                .sId(sId)
                .eId(eId)
                .elasticityCoefficient(elasticityCoefficient)
                .naturalLength(naturalLength)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_SimpleRubber.Builder().elasticityCoefficient(DeponderHelper.DEFAULT_RUBBER_ELASTICITY_COEFFICIENT).naturalLength(DeponderHelper.DEFAULT_RUBBER_NATURAL_LENGTH);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder itemView(View itemView);

        abstract ImmutableList.Builder<BaseOption> vArrBuilder();

        abstract Builder id(String id);

        public abstract Builder sId(String id);

        public abstract Builder eId(String eId);

        abstract Builder animator(NAnimator animator);

        abstract Builder matrix(Matrix matrix);

        public abstract Builder elasticityCoefficient(float elasticityCoefficient);

        public abstract Builder naturalLength(int naturalLength);

        Builder addV(BaseOption... options) {
            vArrBuilder().add(options);
            return this;
        }

        abstract SimpleRubber autoBuild();

        abstract String sId();

        abstract String eId();

        abstract View itemView();

        abstract Matrix matrix();

        public final SimpleRubber build() {
            if (TextUtils.equals(sId(), eId()))
                throw new IllegalStateException("Start and End points cannot be same");

            Observable.just(itemView())
                    .ofType(ViewGroup.class)
                    .flatMap(new Function<ViewGroup, ObservableSource<View>>() {
                        final AtomicInteger integer = new AtomicInteger();

                        @Override
                        public ObservableSource<View> apply(ViewGroup viewGroup) {
                            return Observable.just(viewGroup.getChildAt(integer.getAndIncrement()));
                        }
                    })
                    .filter(view -> {
                        if (view.getTag() instanceof String) {
                            String tag = (String) view.getTag();
                            return TextUtils.equals(tag, DeponderHelper.TAG_OF_UN_RUBBER);
                        }
                        return false;
                    })
                    .map((Function<View, BaseOption>) view -> new BaseOption() {
                        @NonNull
                        @Override
                        public View itemView() {
                            return view;
                        }

                        @NonNull
                        @Override
                        public String id() {
                            return String.valueOf(view.getId());
                        }

                        @NonNull
                        @Override
                        public NAnimator animator() {
                            return new NAnimator(matrix());
                        }

                        @NonNull
                        @Override
                        public Matrix matrix() {
                            return view.getMatrix();
                        }

                    }).blockingSubscribe(this::addV);

            return id(DeponderHelper.concatId(sId(),eId()))
                    .matrix(itemView().getMatrix())
                    .animator(new NAnimator(matrix()))
                    .autoBuild();
        }
    }
}
