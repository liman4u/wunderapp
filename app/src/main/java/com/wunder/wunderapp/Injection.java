/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wunder.wunderapp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.wunder.wunderapp.database.source.CarsDataSource;
import com.wunder.wunderapp.database.source.CarsRepository;
import com.wunder.wunderapp.database.source.local.CarsDatabase;
import com.wunder.wunderapp.database.source.local.CarsLocalDataSource;
import com.wunder.wunderapp.database.source.remote.CarsRemoteDataSource;
import com.wunder.wunderapp.utils.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of production implementations for
 * {@link CarsDataSource} at compile time.
 */
public class Injection {

    public static CarsRepository provideCarsRepository(@NonNull Context context) {
        checkNotNull(context);
        CarsDatabase database = CarsDatabase.getInstance(context);
        return CarsRepository.getInstance(CarsRemoteDataSource.getInstance(context),
                CarsLocalDataSource.getInstance(new AppExecutors(),
                        database.carsDao()));

    }
}
