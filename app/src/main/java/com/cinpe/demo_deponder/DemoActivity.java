package com.cinpe.demo_deponder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.cinpe.demo_deponder.databinding.ActivityDemoBinding;
import com.cinpe.deponder.DeponderProxy;
import com.cinpe.deponder.model.MyPlanetOption;
import com.cinpe.deponder.model.MyRubberOption;
import com.cinpe.deponder.option.PlanetOption;
import com.cinpe.deponder.option.RubberOption;

import java.util.ArrayList;
import java.util.List;


/**
 * An example activity that shows how to use DeponderProxy.
 */
public class DemoActivity extends AppCompatActivity implements DemoActivityControl {

    private static final String TAG = "DemoActivity";

    private ActivityDemoBinding mBinding;

    DeponderProxy<MyPlanetOption, MyRubberOption> deponderProxy;


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo);

        deponderProxy = new DeponderProxy<MyPlanetOption, MyRubberOption>(mBinding.layoutRoot) {
            @Override
            public MyPlanetOption functionP(View p) {
                return MyPlanetOption
                        .builder()
                        .itemView(p)
                        .id(String.valueOf(p.hashCode()))
                        .build();
            }

//            @Override
//            public RubberOption functionR(View r) {
////                return null;
//                return MyRubberOption
//                        .builder()
//                        .id()
//                        .eId()
//                        .itemView(r)
//                        .build();
//            }
        };

        incubateDate2();

        Log.i(TAG, "[submit]childCount:" + mBinding.layoutRoot);
    }

    @Override
    public void onClickPlanet(View view) {
        Log.d(TAG, "onClickPlanet() called with: view = [" + view + "]");
    }

    @Override
    public void onClickAddPO(View view) {

    }

    @Override
    public void onClickAddRO(View view) {

    }

    /**
     * 随便生成些数据.
     */
    private void incubateDate() {

        List<View> list = new ArrayList<>();
        list.add(mBinding.item0);
//        list.add(mBinding.item1);
//        list.add(mBinding.item2);

        //提交view集合.
        deponderProxy.submit(list);
    }


    /**
     * 随便生成些数据2.
     */
    private void incubateDate2() {

        List<View> list = new ArrayList<>();
        list.add(mBinding.item0);
//        list.add(mBinding.item1);
//        list.add(mBinding.item2);

        List<MyRubberOption> rubberOptions = new ArrayList<>();

//        MyRubberOption rubberOption = MyRubberOption.builder()
//                .id(String.valueOf(mBinding.item0.hashCode()))
//                .eId(String.valueOf(mBinding.item1.hashCode()))
//                .naturalLength(300)
//                .itemView(DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.rubber_demo, mBinding.layoutRoot, false).getRoot())
//                .build();

//        rubberOptions.add(rubberOption);

        //提交view集合.
//        mBinding.item0.post(()->deponderProxy.submit(list, null, 1f));
        deponderProxy.submit(list, null, 1f);
    }
}