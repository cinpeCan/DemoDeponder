package com.cinpe.deponder.control;

import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;

/**
 * @Description: 描述
 * @Author: Cinpe
 * @E-Mail: cinpeCan@outlook.com
 * @CreateDate: 2021/12/22
 * @Version: 0.01
 */
public interface DeponderControl<PO, RO> {

    void submitPlanet(@NonNull Collection<PO> pList);

    void submitRubber(@NonNull Collection<RO> rList);

    void submitScale(@FloatRange(from = 0, fromInclusive = false) float scale);

}
